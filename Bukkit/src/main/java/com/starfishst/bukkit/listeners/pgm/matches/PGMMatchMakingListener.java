package com.starfishst.bukkit.listeners.pgm.matches;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.client.BukkitBooleanRequest;
import com.starfishst.bukkit.client.BukkitRequest;
import com.starfishst.bukkit.commands.ReadyCommand;
import com.starfishst.bukkit.listeners.pgm.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.listeners.pgm.matches.creation.RandomTeamCreation;
import com.starfishst.bukkit.listeners.pgm.matches.creation.TeamCreation;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import me.googas.api.client.data.SimpleMatch;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.matches.Ladder;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;

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

  /** The list of matches hosted by the server */
  @NotNull private final List<HostedMatch> matches = new ArrayList<>();

  /** The users to add in case they could not be added */
  @NotNull private final Map<UUID, Party> toAdd = new HashMap<>();

  /**
   * Checks whether this match maker can host a match
   *
   * @param match the match querying to be hosted
   * @return true if the match can be hosted
   */
  @Receptor("can-host")
  public boolean canHost(@ParamName("match") SimpleMatch match) {
    this.wakeUpServer();
    Logger logger = Guido.getLogger();
    String type = match.getDetails().getOr("type", String.class, "none");
    String ladderName = match.getDetails().get("ladder", String.class);
    Match pgmMatch = this.getCurrentPgm();
    logger.info("Received info to host match \n " + match);
    if (type.equalsIgnoreCase("pgm")
        && ladderName != null
        && PGM.get().isEnabled()
        && (pgmMatch == null || !pgmMatch.isRunning())) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              new BukkitRequest<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName).append("guild", match.getGuildId()).build())
                  .send();
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
  public String host(@ParamName("match") SimpleMatch match) {
    String type = match.getDetails().getOr("type", String.class, "none");
    String ladderName = match.getDetails().get("ladder", String.class);
    PGM pgm = PGM.get();
    if (type.equalsIgnoreCase("PGM") && ladderName != null) {
      JsonClient connection = Guido.getClient().getConnection();
      if (connection != null) {
        try {
          Ladder ladder =
              new BukkitRequest<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("name", ladderName).append("guild", match.getGuildId()).build())
                  .send();
          if (ladder != null) {
            List<MapInfo> maps = this.getSuitableMaps(ladder);
            if (!maps.isEmpty()) {
              MapInfo random = RandomUtils.getRandom(maps);
              Match loaded = pgm.getMatchManager().createMatch(random.getId()).get();
              HostedMatch hostedMatch =
                  new HostedMatch(
                      match.getId(),
                      ladderName,
                      match.getParticipants(),
                      random,
                      loaded.getId(),
                      new SimpleValuesMap(
                              "team-selection",
                              ladder.getOptions().getOr("team-selection", String.class, "random"))
                          .put("players-per-team", ladder.playersPerTeam()));
              this.matches.add(hostedMatch);
              new BukkitBooleanRequest(
                      "match-remove-team-by-id",
                      Maps.objects("id", match.getId()).append("team", -2).build())
                  .send(bol -> {});
              TeamCreation teamCreation =
                  this.creator.get(
                      hostedMatch.getDetails().getOr("team-selection", String.class, "random"));
              teamCreation.createTeams(this, hostedMatch, loaded);
              Server server = Bukkit.getServer();
              return server.getIp() + ":" + server.getPort();
            }
          }
        } catch (MessengerListenFailException | InterruptedException | ExecutionException e) {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Get the map that is going to be played in the match
   *
   * @param pgm the pgm instance to set the next map
   * @param maps the maps available for the match
   * @return the random selected map
   */
  @NotNull
  public MapInfo getMatchMap(@NotNull PGM pgm, @NotNull List<MapInfo> maps) {
    MapInfo random = RandomUtils.getRandom(maps);
    pgm.getMapOrder().setNextMap(random);
    return random;
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
   * Get a hosted match by its id
   *
   * @param matchId the id of the match to get
   * @return the match if found null otherwise
   */
  @Nullable
  public HostedMatch getMatch(@NotNull String matchId) {
    for (HostedMatch match : this.matches) {
      if (match.getId().equals(matchId)) return match;
    }
    return null;
  }

  /**
   * Get the match where a command sender is participating
   *
   * @param sender the sendedr to get the match
   */
  @Nullable
  public HostedMatch getMatch(@NotNull CommandSender sender) {
    for (HostedMatch match : this.matches) {
      if (match.isParticipating(sender)) return match;
    }
    return null;
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
    HostedMatch hostedMatch = this.getMatchByPgm(event.getMatch().getId());
    if (hostedMatch == null) return;
    int winnersId = hostedMatch.getTeamId(this.getWinnersId(event));
    if (connection != null) {
      new BukkitBooleanRequest(
              "match-finish-id",
              Maps.objects("id", hostedMatch.getId()).append("winners", winnersId))
          .send(bol -> {});
      this.matches.remove(hostedMatch);
    }
    this.toAdd.clear();
    this.clearTeamsReady();
    this.cleanCreators();
  }

  /**
   * Get the id of the team that won the match
   *
   * @param event the event of a match finishing
   * @return the id of the winners of the match
   */
  @Nullable
  public String getWinnersId(@NotNull MatchFinishEvent event) {
    for (Competitor winner : event.getWinners()) {
      return winner.getId();
    }
    return null;
  }

  /** Clean the team creators */
  public void cleanCreators() {
    for (TeamCreation value : this.creator.values()) {
      value.clear();
    }
  }

  /** Clear all the teams that are ready. This is used so the command /ready can be used */
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

  @NotNull
  public Map<UUID, Party> getToAdd() {
    return this.toAdd;
  }

  @Nullable
  public TeamCreation getCreation(@NotNull String key) {
    return this.creator.get(key);
  }

  /**
   * Get a match by the id of the pgm match id that is hosting it
   *
   * @param pgmId the id of the pgm match
   * @return the match if found else null
   */
  @Nullable
  public HostedMatch getMatchByPgm(@NotNull String pgmId) {
    for (HostedMatch match : this.matches) {
      if (match.getPgmId().equals(pgmId)) return match;
    }
    return null;
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
