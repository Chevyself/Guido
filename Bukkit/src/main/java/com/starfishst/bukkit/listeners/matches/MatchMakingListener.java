package com.starfishst.bukkit.listeners.matches;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import me.googas.api.client.data.MatchImpl;
import me.googas.api.client.data.TeamImpl;
import me.googas.api.client.data.TeamMemberImpl;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
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
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.cycle.CycleMatchModule;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Match makes */
public class MatchMakingListener implements GuidoListener {

  /** The id of the match that is being hosted */
  @Nullable private String matchId;

  /** The participants of the match */
  @NotNull private final Collection<LinkedInfo> participants = new HashSet<>();
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

  @Receptor("can-host")
  public boolean canHost(@ParamName("match") MatchImpl match) {
    Logger logger = Guido.getLogger();
    String type = match.getDetails().getValueOr("type", String.class, "none");
    String ladderName = match.getDetails().getValue("ladder", String.class);
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
    String type = match.getDetails().getValueOr("type", String.class, "none");
    String ladderName = match.getDetails().getValue("ladder", String.class);
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
              }
              this.nextMap = random;
              this.perTeam = ladder.playersPerTeam();
              this.participants.addAll(match.getParticipants());
              this.matchId = match.getId();
              this.teamSelection =
                  match.getDetails().getValueOr("team-selection", String.class, "random");
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
        String.valueOf(
            connection != null && match.getMap().equals(this.nextMap) && this.matchId != null));
    if (connection != null && match.getMap().equals(this.nextMap) && this.matchId != null) {
      this.pgmMatchId = match.getId();
      logger.info("PGM Match id: " + this.pgmMatchId + " and " + this.matchId);
      switch (this.teamSelection) {
        case "random":
        default:
          List<LinkedInfo> participantsCopy = new ArrayList<>(this.participants);
          List<List<LinkedInfo>> teams = new ArrayList<>();
          List<Party> occupied = new ArrayList<>();
          Party observers = event.getMatch().getDefaultParty();
          for (int i = 0; i < (this.participants.size() / this.perTeam); i++) {
            List<LinkedInfo> aTeam = RandomUtils.getRandom(participantsCopy, this.perTeam);
            teams.add(aTeam);
            participantsCopy.removeAll(aTeam);
          }
          logger.info("Teams have been randomly selected: " + teams);
          for (List<LinkedInfo> team : teams) {
            for (Party party : event.getMatch().getParties()) {
              if (party != observers && !occupied.contains(party) && party instanceof Team) {
                occupied.add(party);
                Set<TeamMember> members = new HashSet<>();
                for (LinkedInfo info : team) {
                  UUID uuid =
                      UUIDUtils.untrim(
                          info.getIdentification().getValidatedValue("uuid", String.class));
                  MatchPlayer player = match.getPlayer(uuid);
                  if (player != null) {
                    match.setParty(player, party);
                  }
                  this.toAdd.put(uuid, party);
                  members.add(new TeamMemberImpl(info, TeamRole.NORMAL));
                }
                logger.info(party + " has been assigned to " + team);
                String name = "Team " + (teams.indexOf(team) + 1);
                ((Team) party).setName(name);
                connection.sendRequest(
                    new Request<>(
                        Boolean.class,
                        "match-add-team",
                        Maps.objects("id", this.matchId)
                            .append("team", new TeamImpl(-3, name, members))
                            .build()),
                    bol -> {
                      logger.info("Team " + name + " was added to " + this.matchId + "? " + bol);
                    });
                break;
              }
            }
          }
          match.needModule(StartMatchModule.class).forceStartCountdown();
          break;
      }
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
  public Collection<LinkedInfo> getParticipants() {
    return this.participants;
  }

  @NotNull
  public Map<UUID, Party> getToAdd() {
    return this.toAdd;
  }

  @NotNull
  public MapInfo getNextMap() {
    return this.nextMap;
  }

  public String getPgmMatchId() {
    return this.pgmMatchId;
  }

  public String getTeamSelection() {
    return this.teamSelection;
  }

  public int getPerTeam() {
    return this.perTeam;
  }
}
