package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.matches.HostedPlayer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.Lots;
import me.googas.commons.RandomUtils;
import me.googas.commons.builder.Builder;
import me.googas.commons.maps.Maps;
import me.googas.commons.scheduler.Countdown;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.json.client.JsonClient;
import me.googas.starbox.Starbox;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Select a teammate by picking it */
public class PickTeamSelection implements TeamCreation {

  /** The time for a leader to pick */
  private static final Time timeToPick = Time.fromString("1m");

  /** The map of the match and it's players left to select */
  @NonNull private final Map<String, Set<HostedPlayer>> playersLeft = new HashMap<>();

  /** The teams that were selected in the id of the match */
  @NonNull private final Map<String, List<SelectingTeam>> teams = new HashMap<>();

  /** The uuid of the leader that is currently picking */
  @NonNull private final Map<String, UUID> currently = new HashMap<>();

  @NonNull private final Map<UUID, Countdown> tasks = new HashMap<>();

  /**
   * Get the appropriated team name
   *
   * @param captain the leader of the team
   * @param extra an extra string to add in the team name
   * @return the name of the team
   */
  @NonNull
  public String getTeamName(HostedPlayer captain, @NonNull String extra) {
    String nickname = captain.getRecogString("nickname", "");
    if (!nickname.isEmpty()) {
      return nickname + "'s team";
    } else {
      return "Team " + extra;
    }
  }

  /**
   * Get the pgm team that the team can use
   *
   * @param matchId the id of the match to check if the party is occupied
   * @param match the match to getId the teams from
   * @return the team
   * @throws IllegalStateException if there's no team available which means that the map picked was
   *     incorrect
   */
  @NonNull
  public Team getParty(@NonNull String matchId, @NonNull Match match) {
    Party observers = match.getDefaultParty();
    for (Party party : match.getParties()) {
      if (party != observers && !this.isOccupied(matchId, party) && party instanceof Team) {
        return (Team) party;
      }
    }
    throw new IllegalStateException("There was no appropriated amount of teams to give one next");
  }

  /**
   * Pick two random leaders
   *
   * @param matchId the id of the match to getId the leaders
   * @return the random leaders
   */
  @NonNull
  public Map<HostedPlayer, TeamMember> pickLeaders(@NonNull String matchId) {
    Map<HostedPlayer, TeamMember> captains = new HashMap<>();
    Set<HostedPlayer> playersLeft = this.getPlayersLeft(matchId);
    for (HostedPlayer hosted : RandomUtils.getRandom(new ArrayList<>(playersLeft), 2)) {
      captains.put(hosted, new TeamMember(hosted.toLink(), TeamRole.LEADER));
      playersLeft.remove(hosted);
    }
    return captains;
  }

  /**
   * Get the bukkit player from linked info
   *
   * @param info the info to getId the player from
   * @return the player but if not online it will be null
   */
  public Player getPlayer(@NonNull LinkableInfo info) {
    return Bukkit.getPlayer(this.getUuid(info));
  }

  /**
   * Get the uuid from the linked information
   *
   * @param info the linked information
   * @return the uuid
   */
  @NonNull
  public UUID getUuid(@NonNull LinkableInfo info) {
    return info.getIdUUID("uuid", true);
  }

  /**
   * Get the selecting team from a member
   *
   * @param matchId the id of the match to getId the selecting team
   * @param captain the captain of the team
   * @return the team if the given team member is a captain from a team
   */
  private SelectingTeam getSelecting(@NonNull String matchId, @NonNull TeamMember captain) {
    for (SelectingTeam team : this.teams.getOrDefault(matchId, new ArrayList<>())) {
      if (team.getLeader().equals(captain)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Make the captain pick a player
   *
   * @param matchId the id of the match to getId the information for the next pick
   * @param captain the captain picking a player
   * @param hosted the link info of the player
   * @throws IllegalArgumentException if the captain argument is not a captain or if the captain is
   *     not currently picking
   */
  public void pick(
      @NonNull String matchId, @NonNull TeamMember captain, @NonNull HostedPlayer hosted) {
    SelectingTeam selecting = this.getSelecting(matchId, captain);
    if (selecting != null && this.isPicking(matchId, captain)) {
      UUID uuid = this.getUuid(captain.getLink());
      Countdown countdown = this.tasks.get(uuid);
      if (countdown != null) {
        countdown.cancel();
        this.tasks.remove(uuid);
      }
      TeamMember teamMember = new TeamMember(hosted.toLink(), TeamRole.NORMAL);
      selecting.getMembers().add(teamMember);
      Team party = selecting.getParty();
      Match match = party.getMatch();
      this.setParty(hosted, party, match);
      Collection<HostedPlayer> playersLeft = this.getPlayersLeft(matchId);
      playersLeft.remove(hosted);
      this.nextPick(matchId, selecting);
    } else {
      if (selecting == null) {
        throw new IllegalArgumentException(captain + " is not a captain of a team");
      }
      if (!this.isPicking(matchId, captain)) {
        throw new IllegalArgumentException(captain + " is not currently picking");
      }
    }
  }

  /**
   * Sets the next captain that is picking the match or starts the match in case there's no players
   * left
   *
   * @param matchId the id of the match waiting for the next pick
   * @param team the team that just selected a player
   */
  public void nextPick(@NonNull String matchId, @NonNull SelectingTeam team) {
    Collection<HostedPlayer> playersLeft = this.getPlayersLeft(matchId);
    if (playersLeft.isEmpty()) {
      // Starts the match and finishes picking
      PGMMatchMakingHandler listener =
          Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
      PGMHostedMatch match = listener.getMatch(matchId);
      if (match == null) return;
      Match matchPgm = match.toPGM();
      if (matchPgm == null) return;
      matchPgm
          .needModule(StartMatchModule.class)
          .forceStartCountdown(
              Duration.ofSeconds(PGMMatchMakingHandler.secondsToStart), Duration.ZERO);
      for (SelectingTeam selectingTeam : this.getTeams(matchId)) {
        MatchTeam construct = selectingTeam.build();
        JsonClient connection = Guido.getClient().getConnection();
        Requests.Matches.addTeam(matchId, construct)
            .send(
                connection,
                optional -> {
                  optional.ifPresent(
                      id ->
                          match
                              .getTeams()
                              .put(
                                  selectingTeam.getParty().getId(),
                                  selectingTeam.setId(id).build()));
                });
        Requests.Matches.status(matchId, MatchStatus.STARTING).queue(connection);
      }
      this.clear(matchId);
    } else if (playersLeft.size() == 2) {
      this.nextLeader(matchId, team.getLeader());
    } else {
      TeamMember leader = this.getNext(matchId, team).getLeader();
      if (playersLeft.size() == 1) {
        this.nextLeader(matchId, leader);
        for (HostedPlayer hosted : playersLeft) {
          this.pick(matchId, leader, hosted);
          break;
        }
      } else {
        this.nextLeader(matchId, this.getNext(matchId, team).getLeader());
      }
    }
  }

  private void clear(@NonNull String id) {
    this.playersLeft.remove(id);
  }

  /**
   * Set the next leader to pick the next player
   *
   * @param matchId the id of the match to put the seconds left and the leader
   * @param nextLeader the next leader to play the match
   */
  public void nextLeader(@NonNull String matchId, @NonNull TeamMember nextLeader) {
    this.nextLeaderTask(matchId, nextLeader);
    Player player = this.getPlayer(nextLeader.getLink());
    if (player != null) {
      player.sendMessage(
          Guido.getLanguageHandler()
              .getFile(player)
              .get(
                  "match-making.pick.next",
                  Maps.singleton("picks", Lots.pretty(this.getParticipantsNames(matchId)))));
    }
  }

  /**
   * Get the next team that has to pick
   *
   * @param matchId the id of the match to getId the collection of teams
   * @param team the current team picking
   * @return the next team to pick
   */
  @NonNull
  public SelectingTeam getNext(@NonNull String matchId, @NonNull SelectingTeam team) {
    List<SelectingTeam> teams = this.getTeams(matchId);
    if (teams.indexOf(team) == teams.size() - 1) {
      return teams.get(0);
    } else {
      return teams.get(teams.indexOf(team) + 1);
    }
  }

  /**
   * Checks whether a pgm party is already occupied by another team
   *
   * @param matchId the match to getId the collection of currently playing teams
   * @param party the party to check
   * @return true if there's a team assigned to the party else null
   */
  private boolean isOccupied(@NonNull String matchId, @NonNull Party party) {
    for (SelectingTeam team : this.getTeams(matchId)) {
      if (team.getParty().equals(party)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a team member is currently picking
   *
   * @param matchId the match id to getId the member
   * @param member the member to check if it is picking
   * @return true if it is picking
   */
  public boolean isPicking(@NonNull String matchId, @NonNull TeamMember member) {
    return this.getUuid(member.getLink()).equals(this.currently.get(matchId));
  }

  /**
   * Get the collection of players left to pick
   *
   * @param matchId the id of the match to getId the players left
   * @return the players left to pick
   */
  @NonNull
  public Set<HostedPlayer> getPlayersLeft(@NonNull String matchId) {
    return this.playersLeft.computeIfAbsent(matchId, string -> new HashSet<>());
  }

  /**
   * Get all the teams that are currently to pick
   *
   * @param matchId the id of the match to getId the collection
   * @return the team
   */
  @NonNull
  public List<SelectingTeam> getTeams(@NonNull String matchId) {
    return this.teams.computeIfAbsent(matchId, string -> new ArrayList<>());
  }

  /**
   * Return the nicknames of the participants of the match
   *
   * @param matchId the match to match and getId the players left
   * @return the names of the participants
   */
  @NonNull
  public List<String> getParticipantsNames(@NonNull String matchId) {
    List<String> names = new ArrayList<>();
    for (HostedPlayer player : this.getPlayersLeft(matchId)) {
      String nickname = player.getNickname();
      names.add(nickname);
    }
    return names;
  }

  public void nextLeaderTask(@NonNull String id, @NonNull TeamMember leader) {
    UUID uuid = this.getUuid(leader.getLink());
    this.currently.put(id, uuid);
    Countdown countdown =
        Starbox.getScheduler()
            .countdown(
                PickTeamSelection.timeToPick,
                (left) -> {
                  if (!this.isPicking(id, leader)) return;
                  long secondsLeft = left.getValue(Unit.SECONDS);
                  if (secondsLeft <= 10 || secondsLeft % 5 == 0) {
                    Player player = this.getPlayer(leader.getLink());
                    if (player != null) {
                      BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
                      player.sendMessage(
                          locale.get(
                              "match-making.pick.time-left",
                              Maps.builder(
                                      "time",
                                      new Time(secondsLeft, Unit.SECONDS).toEffectiveString())
                                  .append("picks", Lots.pretty(this.getParticipantsNames(id)))));
                    }
                  }
                },
                () -> {
                  if (!this.isPicking(id, leader)) return;
                  this.pick(id, leader, RandomUtils.getRandom(this.getPlayersLeft(id)));
                });
    this.tasks.put(uuid, countdown);
  }

  /** Clears the team creator */
  @Override
  public void clear() {
    this.playersLeft.clear();
    this.teams.clear();
    this.currently.clear();
  }

  @Override
  public void createTeams(
      @NonNull PGMMatchMakingHandler matchMaking,
      @NonNull PGMHostedMatch hostedMatch,
      @NonNull Match match) {
    this.playersLeft.put(hostedMatch.getId(), hostedMatch.getParticipants());
    Map<HostedPlayer, TeamMember> leaders = this.pickLeaders(hostedMatch.getId());
    AtomicInteger index = new AtomicInteger(1);
    leaders.forEach(
        (hosted, leader) -> {
          Team teamParty = this.getParty(hostedMatch.getId(), match);
          Player player = this.getPlayer(leader.getLink());
          String teamName = this.getTeamName(hosted, String.valueOf(index.getAndIncrement()));
          this.teams
              .computeIfAbsent(hostedMatch.getId(), string -> new ArrayList<>())
              .add(new SelectingTeam(teamParty, leader));
          teamParty.setName(teamName);
          this.setParty(hosted, teamParty, teamParty.getMatch());
          if (player != null) {
            BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
            player.sendMessage(locale.get("match-making.pick.leader"));
          }
        });
    this.nextLeader(hostedMatch.getId(), RandomUtils.getRandom(leaders.values()));
  }

  /**
   * This object represents a team which is going to play the match but it is still selecting
   * players
   */
  public class SelectingTeam implements Builder<MatchTeam> {

    /** The pgm party assigned to this team */
    @NonNull @Getter private final Team party;

    /** The leader of the team */
    @NonNull @Getter private final TeamMember leader;

    /** The member of the team */
    @NonNull @Getter private final Collection<TeamMember> members = new HashSet<>();

    /** The actual id of the team */
    @Getter private int id = -3;

    /**
     * Create the team
     *
     * @param party the pgm party assigned to this team
     * @param leader the leader which is selecting players
     */
    SelectingTeam(@NonNull Team party, @NonNull TeamMember leader) {
      this.party = party;
      this.leader = leader;
    }

    public SelectingTeam setId(int id) {
      this.id = id;
      return this;
    }

    @NonNull
    public UUID getLeaderUniqueId() {
      return PickTeamSelection.this.getUuid(this.leader.getLink());
    }

    @Override
    public @NonNull MatchTeam build() {
      Set<TeamMember> copy = new HashSet<>(this.members);
      copy.add(this.leader);
      return new MatchTeam(this.id, copy, this.party.getNameLegacy());
    }
  }
}
