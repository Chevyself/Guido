package com.starfishst.bukkit.listeners.pgm.matches;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.commands.ReadyCommand;
import com.starfishst.bukkit.listeners.pgm.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.pgm.matches.creation.RandomTeamCreation;
import com.starfishst.bukkit.listeners.pgm.matches.creation.TeamCreation;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import me.googas.api.client.data.MatchImpl;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Ladder;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.cycle.CycleMatchModule;

/** Creates matches for the bot */
public class PGMMatchMakingListener implements GuidoListener {

  /** The seconds to start the matches */
  public static final int secondsToStart = 120;

  /** The team creators for matches */
  @NotNull
  private final Map<String, TeamCreation> creator =
      Maps.builder("random", (TeamCreation) new RandomTeamCreation())
          .append("pick", new PickTeamSelection())
          .build();

  /** The id of the match that is being hosted */
  @Nullable private String matchId;

  /** The participants of the match */
  @NotNull private final Collection<LinkableInfo> participants = new HashSet<>();
  /** The users to add in case they could not be added */
  @NotNull private final Map<UUID, Party> toAdd = new HashMap<>();
  /** The next map which will be played */
  @Nullable private MapInfo nextMap;
  /** The match id of the pgm match */
  @Nullable private String pgmMatchId;
  /** The way to select teams */
  @NotNull private String teamSelection = "random";
  /** The amount of players per team */
  private int perTeam = 0;

  /**
   * Checks whether this match maker can host a match
   *
   * @param match the match querying to be hosted
   * @return true if the match can be hosted
   */
  @Receptor("can-host")
  public boolean canHost(@ParamName("match") MatchImpl match) {
    this.wakeUpServer();
    Logger logger = Guido.getLogger();
    String type = match.getDetails().getOr("type", String.class, "none");
    String ladderName = match.getDetails().get("ladder", String.class);
    Match pgmMatch = this.getCurrentPgm();
    logger.info("Received info to host match \n " + match);
    if (type.equalsIgnoreCase("pgm")
        && ladderName != null
        && (pgmMatch == null || !pgmMatch.isRunning())) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              connection.sendRequest(
                  new Request<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName)
                          .append("guild", match.getGuildId())
                          .build()));
          if (ladder != null) {
            logger.info("Ladder exists if there's maps then the match will be hosted");
            return !this.getSuitableMaps(ladder).isEmpty();
          }
        } catch (MessengerListenFailException e) {
          e.printStackTrace();
          return false;
        }
      }
    }
    return false;
  }

  /**
   * get all the map that are suitable for a ladder
   *
   * @param ladder the ladder
   * @return the list of suitable maps
   */
  @NotNull
  public List<MapInfo> getSuitableMaps(@NotNull Ladder ladder) {
    Logger logger = Guido.getLogger();
    List<MapInfo> suitableMaps = new ArrayList<>();
    Iterator<MapInfo> iterator = PGM.get().getMapLibrary().getMaps();
    while (iterator.hasNext()) {
      MapInfo map = iterator.next();
      Collection<Integer> maxPlayers = map.getMaxPlayers();
      if (maxPlayers.size() == 2) {
        int sum = 0;
        int required = ladder.playersPerTeam() * 2;
        for (int maxPlayer : maxPlayers) {
          sum += maxPlayer;
        }
        logger.info(map + " sizes: " + sum + " required " + required);
        if (sum == required) {
          suitableMaps.add(map);
        }
      }
    }
    logger.info("Suitable maps for " + ladder + ": " + suitableMaps);
    return suitableMaps;
  }

  /**
   * Reads host requests
   *
   * @param match the match requesting to be hosted
   * @return the ip of the server if it can be hosted
   */
  @Receptor("host")
  public String host(@ParamName("match") MatchImpl match) {
    Logger logger = Guido.getLogger();
    String type = match.getDetails().getOr("type", String.class, "none");
    String ladderName = match.getDetails().get("ladder", String.class);
    if (type.equalsIgnoreCase("PGM")) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              connection.sendRequest(
                  new Request<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName)
                          .append("guild", match.getGuildId())
                          .build()));
          if (ladder != null) {
            List<MapInfo> maps = this.getSuitableMaps(ladder);
            if (!maps.isEmpty()) {
              MapInfo random = RandomUtils.getRandom(maps);
              PGM.get().getMapOrder().setNextMap(random);
              Match next = this.getCurrentPgm();
              if (next != null) {
                next.needModule(CycleMatchModule.class).cycleNow();
              } else {
                PGM.get().getMatchManager().createMatch("");
              }
              this.nextMap = random;
              this.perTeam = ladder.playersPerTeam();
              this.participants.addAll(match.getParticipants());
              this.matchId = match.getId();
              this.teamSelection =
                  ladder.getOptions().getOr("team-selection", String.class, "random");
              connection.sendRequest(
                  new Request<>(
                      Boolean.class,
                      "match-remove-team-by-id",
                      Maps.objects("id", this.matchId).append("team", -2).build()),
                  bol -> {
                    logger.info("Was the team participants removed? " + bol);
                  });
              Server server = Bukkit.getServer();
              return server.getIp() + ":" + server.getPort();
            }
          }
        } catch (MessengerListenFailException e) {
          return null;
        }
      }
    }
    return null;
  }

  public void add(@NotNull UUID uuid, @NotNull Party party) {
    MatchManager matchManager = PGM.get().getMatchManager();
    MatchPlayer player = matchManager.getPlayer(uuid);
    if (player != null && player.getMatch().equals(party.getMatch())) {
      party.getMatch().setParty(player, party);
    }
    this.toAdd.put(uuid, party);
  }

  /**
   * Get the current match that is being played by pgm
   *
   * @return the current match being played in pgm
   */
  @Nullable
  private Match getCurrentPgm() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    if (matches.hasNext()) {
      return matches.next();
    }
    return null;
  }

  @EventHandler
  public void onMatchLoadedEvent(MatchLoadEvent event) {
    Logger logger = Guido.getLogger();
    Match match = event.getMatch();
    JsonClient connection = Guido.getClient().getConnection();

    logger.info(
        "Match loaded is it a guido match? "
            + (connection != null && match.getMap().equals(this.nextMap) && this.matchId != null));
    if (connection != null && match.getMap().equals(this.nextMap) && this.matchId != null) {
      this.pgmMatchId = match.getId();
      logger.info("PGM Match id: " + this.pgmMatchId + " and " + this.matchId);
      TeamCreation teamCreation = this.creator.get(this.teamSelection);
      teamCreation.createTeams(this, event.getMatch());
    }
  }

  /**
   * Listen to when a player joins the map to add them to its team
   *
   * @param event the event of
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onMatchPlayerAdded(MatchPlayerAddEvent event) {
    UUID uuid = event.getPlayer().getId();
    Party party = this.toAdd.get(uuid);
    if (party != null) {
      event.setInitialParty(party);
    }
  }

  /**
   * When the match is finished save the data to bot
   *
   * @param event the event of a match finishing
   */
  @EventHandler
  public void onMatchFinish(MatchFinishEvent event) {
    JsonClient connection = Guido.getClient().getConnection();
    Guido.getLogger()
        .info("Saving match " + event.getMatch() + " current match " + this.pgmMatchId);
    if (event.getMatch().getId().equals(this.pgmMatchId)) {
      String winnerName = null;
      for (Competitor winner : event.getWinners()) {
        winnerName = winner.getNameLegacy();
        break;
      }
      if (connection != null) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", this.matchId);
        // TODO this method should not use the winners name but the id of the team!
        map.put("winners", winnerName);
        connection.sendRequest(
            new Request<>(Boolean.class, "match-finish", map),
            bol -> {
              Guido.getLogger().info("Tried to save " + this.matchId + " saved? " + bol);
            });
      }
    }

    this.matchId = null;
    this.nextMap = null;
    this.pgmMatchId = null;
    this.participants.clear();
    this.toAdd.clear();
    this.clearTeamsReady();
    this.cleanCreators();
  }

  /** Clean the team creators */
  public void cleanCreators() {
    for (TeamCreation value : this.creator.values()) {
      value.clear();
    }
  }

  public void clearTeamsReady() {
    for (AnnotatedCommand command : Guido.getCommandManager().getCommands()) {
      if (command.getClazz() instanceof ReadyCommand) {
        ((ReadyCommand) command.getClazz()).clear();
        break;
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "match-making";
  }

  @Nullable
  public String getMatchId() {
    return this.matchId;
  }

  @NotNull
  public Collection<LinkableInfo> getParticipants() {
    return this.participants;
  }

  @NotNull
  public Map<UUID, Party> getToAdd() {
    return this.toAdd;
  }

  public int getPerTeam() {
    return this.perTeam;
  }

  @Nullable
  public TeamCreation getCreation(@NotNull String key) {
    return this.creator.get(key);
  }

  /**
   * In case that the server is suspended this will take care of it.
   *
   * <p>This might be deleted in the future as PGM developers expect to remove dependency in
   * SportPaper
   */
  public void wakeUpServer() {
    Bukkit.getScheduler().runTask(Guido.validated(), () -> BukkitUtils.dispatch("suspend false"));
  }
}
