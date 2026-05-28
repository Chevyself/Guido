package com.starfishst.bukkit.dependencies.pgm.listeners.matches;

import com.github.chevyself.starbox.bukkit.commands.BukkitAnnotatedCommand;
import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.utils.BukkitUtils;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.commands.ReadyCommand;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.PickTeamSelection;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.RandomTeamCreation;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation.TeamCreation;
import com.starfishst.bukkit.matches.HostedPlayer;
import com.starfishst.bukkit.modules.GuidoModule;
import com.starfishst.bukkit.util.ServerUtil;
import com.starfishst.bukkit.util.Tasks;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Maps;
import me.googas.api.utility.RandomUtils;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.net.sockets.json.client.JsonClient;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.events.PlayerParticipationStartEvent;
import tc.oc.pgm.restart.RestartManager;
import tc.oc.pgm.teams.Team;

/** Creates matches for the bot */
public class PGMMatchMakingHandler implements GuidoModule {

  /** The seconds to start the matches */
  // TODO configurable
  public static final int secondsToStart = 120;

  /** The team creators for matches */
  @NonNull
  private final Map<String, TeamCreation> creator =
      Maps.builder("random", (TeamCreation) new RandomTeamCreation())
          .put("pick", new PickTeamSelection())
          .build();

  /** The list of matches hosted by the server */
  @NonNull @Getter private final List<PGMHostedMatch> matches = new ArrayList<>();

  /**
   * The set of uuids of players to ignore in {@link
   * #onParticipationStart(PlayerParticipationStartEvent)}
   */
  @NonNull private final Set<UUID> ignore = new HashSet<>();

  /**
   * Checks whether this match maker can host a match
   *
   * @param match the match querying to be hosted
   * @return true if the match can be hosted
   */
  @Receptor(Requests.MatchServer.CAN_HOST)
  public boolean canHost(@ParamName("match") AbstractMatch match) {
    String type = match.getString(null, "type", "none");
    String ladderName = match.getString(null, "ladder", "no-ladder");
    Match pgmMatch = this.getCurrentPgm();
    JsonClient connection = Guido.getClient().getConnection();
    if (this.check(type, pgmMatch) && connection != null) {
      try {
        Optional<Ladder> optional = Requests.Matches.getLadder(ladderName).send(connection);
        return optional.map(ladder -> !this.getSuitableMaps(ladder).isEmpty()).orElse(false);
      } catch (MessengerListenFailException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public boolean check(String type, Match pgmMatch) {
    return type != null
        && type.equalsIgnoreCase("pgm")
        && PGM.get().isEnabled()
        && !RestartManager.isQueued()
        && (pgmMatch == null
            || pgmMatch.getPhase() == MatchPhase.FINISHED
            || !this.isMatch(pgmMatch));
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
   * getId all the map that are suitable for a ladder
   *
   * @param ladder the ladder
   * @return the list of suitable maps
   */
  @NonNull
  public List<MapInfo> getSuitableMaps(@NonNull Ladder ladder) {
    // TODO add a cache to this
    List<MapInfo> suitableMaps = new ArrayList<>();
    Iterator<MapInfo> iterator = PGM.get().getMapLibrary().getMaps();
    while (iterator.hasNext()) {
      MapInfo map = iterator.next();
      Collection<Integer> maxPlayers = map.getMaxPlayers();
      if (maxPlayers.size() == 2) {
        int sum = 0;
        int required = ladder.playersPerTeam() * 2;
        for (int maxPlayer : maxPlayers) sum += maxPlayer;
        if (sum == required) suitableMaps.add(map);
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
  @Receptor(Requests.MatchServer.HOST)
  public String host(@ParamName("match") AbstractMatch match) {
    if (!this.canHost(match)) return null;
    String type = match.getString(null, "type", "none");
    String ladderName = match.getString(null, "ladder", "no-ladder");
    PGM pgm = PGM.get();
    if (!type.equalsIgnoreCase("PGM") && ladderName == null) return null;
    try {
      JsonClient connection = Guido.getClient().validatedConnection();
      Optional<Ladder> optional = Requests.Matches.getLadder(ladderName).send(connection);
      if (optional.isEmpty()) return null;
      Ladder ladder = optional.get();
      List<MapInfo> maps = this.getSuitableMaps(ladder);
      if (maps.isEmpty()) return null;
      MapInfo random = RandomUtils.getRandom(maps);
      Match loaded = pgm.getMatchManager().createMatch(random.getId()).get();
      PGMHostedMatch hostedMatch =
          new PGMHostedMatch(
              match.getId(),
              HostedPlayer.parse(match.getParticipants()),
              ladderName,
              Maps.singleton(
                  "global",
                  Maps.objects("team-selection", ladder.getString(null, "team-selection", "random"))
                      .build()),
              random,
              loaded.getId());
      this.matches.add(hostedMatch);
      Requests.Matches.status(hostedMatch.getId(), MatchStatus.READY).queue(connection);
      Requests.Matches.detail(hostedMatch.getId(), "map", random.getName()).queue(connection);
      Tasks.sync(() -> this.getTeamCreation(hostedMatch).createTeams(this, hostedMatch, loaded));
      return ServerUtil.getIp();
    } catch (MessengerListenFailException
        | IOException
        | ExecutionException
        | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @NonNull
  public TeamCreation getTeamCreation(PGMHostedMatch hostedMatch) {
    for (String key : this.creator.keySet()) {
      String teamSelection = hostedMatch.getString(null, "team-selection", "random");
      if (key.equalsIgnoreCase(teamSelection)) return this.creator.get(key);
    }
    return this.creator.get("random");
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

  /**
   * Get a hosted match by its id
   *
   * @param matchId the id of the match to getId
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
   * @param sender the sender to getId the match
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
  public void readyToHost(@NonNull JsonClient client) {
    Requests.MatchServer.serverReady().queue(client);
  }

  /**
   * Listen to when a player joins the map to add them to its team
   *
   * @param event the event of
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onMatchPlayerAdded(MatchPlayerAddEvent event) {
    PGMHostedMatch match = this.getMatch(event.getPlayer().getBukkit());
    if (match == null) return;
    Team team = match.getParty(event.getPlayer().getId());
    if (team != null) {
      event.setInitialParty(team);
    }
  }

  @EventHandler
  public void onParticipationStart(PlayerParticipationStartEvent event) {
    PGMHostedMatch match = this.getMatchByPgm(event.getMatch().getId());
    if (match == null || this.ignore.contains(event.getPlayer().getId())) return;
    Team party = match.getParty(event.getPlayer().getId());
    if (party == null || !party.equals(event.getCompetitor())) {
      event.setCancelled(true);
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
    PGMHostedMatch hosted = this.getMatchByPgm(event.getMatch().getId());
    if (hosted == null) return;
    int winnersId = hosted.getTeamId(this.getWinnersId(event));
    if (connection != null) {
      Requests.Matches.finish(hosted.getId(), winnersId).queue(connection);
      this.readyToHost(connection);
    }
    this.matches.remove(hosted);
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
    for (BukkitCommand command : Guido.getCommandManager().getCommands()) {
      if (!(command instanceof BukkitAnnotatedCommand)) continue;
      BukkitAnnotatedCommand annotated = (BukkitAnnotatedCommand) command;
      if (annotated.getObject() instanceof ReadyCommand) {
        ((ReadyCommand) annotated.getObject()).clear();
        break;
      }
    }
  }

  /**
   * Get the team creation for the given key
   *
   * @param key the key to getId what team creation is assigned to
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
      if (match.getPgm().equals(pgmId)) return match;
    }
    return null;
  }

  public void add(@NonNull Match match, @NonNull Party team, @NonNull MatchPlayer player) {
    this.ignore.add(player.getId());
    match.setParty(player, team);
    this.ignore.remove(player.getId());
  }

  /**
   * In case that the server is suspended this will take care of it.
   *
   * <p>This might be deleted in the future as PGM developers expect to remove dependency in
   * SportPaper
   */
  @Deprecated
  public void wakeUpServer() {
    Bukkit.getScheduler()
        .runTask(
            Guido.getPlugin(),
            () -> BukkitUtils.dispatch(Bukkit.getConsoleSender(), "suspend false"));
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

  @Override
  public void onEnable() {
    Guido.getClient().addReceptors(this);
  }

  @Override
  public @NonNull String getName() {
    return "match-making";
  }
}
