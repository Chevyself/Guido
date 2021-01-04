package com.starfishst.bukkit.dependencies.pgm.listeners.matches;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.client.requests.BukkitBooleanRequest;
import com.starfishst.bukkit.client.requests.BukkitRequest;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.commands.ReadyCommand;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.RandomTeamCreation;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.matches.HostedPlayer;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.NonNull;
import me.googas.api.client.data.matches.SimpleMatch;
import me.googas.api.matches.ladder.Ladder;
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
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.restart.RestartManager;

/** Creates matches for the bot */
public class PGMMatchMakingHandler implements Handler {

  /** The seconds to start the matches */
  public static final int secondsToStart = 120;

  /** The team creators for matches */
  @NonNull
  private final Map<String, TeamCreation> creator =
      Maps.builder("random", (TeamCreation) new RandomTeamCreation())
          .append("pick", new PickTeamSelection())
          .build();

  /** The list of matches hosted by the server */
  @NonNull private final List<PGMHostedMatch> matches = new ArrayList<>();

  /** The users to add in case they could not be added */
  @NonNull private final Map<UUID, Party> toAdd = new HashMap<>();

  /**
   * Checks whether this match maker can host a match
   *
   * @param match the match querying to be hosted
   * @return true if the match can be hosted
   */
  @Receptor("server/can-host")
  public boolean canHost(@ParamName("match") SimpleMatch match) {
    this.wakeUpServer();
    String type = match.getDetails().getOr("type", String.class, "none");
    String ladderName = match.getDetails().get("ladder", String.class);
    Match pgmMatch = this.getCurrentPgm();
    if (type.equalsIgnoreCase("pgm")
            && ladderName != null
            && PGM.get().isEnabled()
            && !RestartManager.isQueued()
            && (pgmMatch == null || (pgmMatch.getPhase() == MatchPhase.FINISHED))
        || pgmMatch != null && !this.isMatch(pgmMatch) && !RestartManager.isQueued()) {
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
   * Check whether a pgm match is a simple match
   *
   * @param match the pgm match to check
   * @return true if the match is a host
   */
  private boolean isMatch(@NonNull Match match) {
    return this.getMatchByPgm(match.getId()) != null;
  }

  /**
   * get all the map that are suitable for a ladder
   *
   * @param ladder the ladder
   * @return the list of suitable maps
   */
  @NonNull
  public List<MapInfo> getSuitableMaps(@NonNull Ladder ladder) {
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
        if (sum == required) {
          suitableMaps.add(map);
        }
      }
    }
    return suitableMaps;
  }

  /**
   * Reads host requests
   *
   * @param match the match requesting to be hosted
   * @return the ip of the server if it can be hosted
   */
  @Receptor("server/host")
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
              PGMHostedMatch PGMHostedMatch =
                  new PGMHostedMatch(
                      match.getId(),
                      HostedPlayer.parse(match.getParticipants()),
                      ladderName,
                      match.getDetails(),
                      random,
                      loaded.getId());
              this.matches.add(PGMHostedMatch);
              TeamCreation teamCreation =
                  this.creator.get(
                      PGMHostedMatch.getDetails().getOr("team-selection", String.class, "random"));
              Bukkit.getScheduler()
                  .runTask(
                      Guido.validated(),
                      () -> {
                        teamCreation.createTeams(this, PGMHostedMatch, loaded);
                      });
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
  @NonNull
  public MapInfo getMatchMap(@NonNull PGM pgm, @NonNull List<MapInfo> maps) {
    MapInfo random = RandomUtils.getRandom(maps);
    pgm.getMapOrder().setNextMap(random);
    return random;
  }

  public void add(@NonNull UUID uuid, @NonNull Party party) {
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
  public PGMHostedMatch getMatch(@NonNull String matchId) {
    for (PGMHostedMatch match : this.matches) {
      if (match.getId().equals(matchId)) return match;
    }
    return null;
  }

  /**
   * Get the match where a command sender is participating
   *
   * @param sender the sender to get the match
   */
  public PGMHostedMatch getMatch(@NonNull CommandSender sender) {
    for (PGMHostedMatch match : this.matches) {
      if (match.isParticipating(sender)) return match;
    }
    Match match = PGM.get().getMatchManager().getMatch(sender);
    if (match != null) {
      return this.getMatchByPgm(match.getId());
    }
    return null;
  }

  /** Called when the server is ready to host a match */
  public void readyToHost() {
    new BukkitBooleanRequest("server-ready").send(ignored -> {});
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
    PGMHostedMatch PGMHostedMatch = this.getMatchByPgm(event.getMatch().getId());
    if (PGMHostedMatch == null) return;
    int winnersId = PGMHostedMatch.getTeamId(this.getWinnersId(event));
    if (connection != null) {
      new BukkitBooleanRequest(
              "match-finish-id",
              Maps.objects("id", PGMHostedMatch.getId()).append("winners", winnersId))
          .send(bol -> {});
      this.readyToHost();
      this.matches.remove(PGMHostedMatch);
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
  public String getWinnersId(@NonNull MatchFinishEvent event) {
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

  /**
   * Get the team creation for the given key
   *
   * @param key the key to get what team creation is assigned to
   * @return the team creation which is assigned to the key
   */
  public TeamCreation getCreation(@NonNull String key) {
    return this.creator.get(key);
  }

  /**
   * Get a match by the id of the pgm match id that is hosting it
   *
   * @param pgmId the id of the pgm match
   * @return the match if found else null
   */
  public PGMHostedMatch getMatchByPgm(@NonNull String pgmId) {
    for (PGMHostedMatch match : this.matches) {
      if (match.toPGM().equals(pgmId)) return match;
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

  /**
   * Get the current match that is being played by pgm
   *
   * @return the current match being played in pgm
   */
  private Match getCurrentPgm() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    if (matches.hasNext()) {
      return matches.next();
    }
    return null;
  }

  /**
   * Get the map of player to add. This map contains the uuid of the player and the party they are
   * on
   *
   * @return the map
   */
  @NonNull
  public Map<UUID, Party> getToAdd() {
    return this.toAdd;
  }

  @Override
  public boolean hasReceptors() {
    return true;
  }

  @Override
  public void onDisable() {}

  @Override
  public @NonNull String getName() {
    return "match-making";
  }
}
