package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.client.BukkitIntRequest;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.HostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.Lots;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.api.MessengerListenFailException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Select a teammate by picking it */
public class PickTeamSelection implements TeamCreation {

  /** The time for a leader to pick */
  private static final long timeToPick = Time.fromString("1m").getValue(Unit.SECONDS);

  /** The map of the match and it's players left to select */
  @NonNull private final Map<String, Collection<LinkableInfo>> playersLeft = new HashMap<>();

  /** The teams that were selected in the id of the match */
  @NonNull private final Map<String, List<SelectingTeam>> teams = new HashMap<>();

  /** The current leader picking players on its match */
  @NonNull private final Map<String, TeamMember> currentLeader = new HashMap<>();

  /** The time left for the leader to pick */
  private final Map<String, Long> secondsLeft = new HashMap<>();

  /** Create the team selection by picking the players */
  public PickTeamSelection() {
    Bukkit.getScheduler()
        .runTaskTimer(
            Guido.validated(),
            () -> {
              for (String string : this.currentLeader.keySet()) {
                TeamMember leader = this.currentLeader.get(string);
                if (leader == null) return;
                this.secondsLeft.put(
                    string,
                    this.secondsLeft.getOrDefault(string, PickTeamSelection.timeToPick) - 1L);
                if (this.secondsLeft.getOrDefault(string, PickTeamSelection.timeToPick) < 1) {
                  this.pick(
                      string,
                      leader,
                      RandomUtils.getRandom(new ArrayList<>(this.getPlayersLeft(string))));
                } else {
                  long secondsLeft = this.getSecondsLeft(string);
                  if (secondsLeft <= 10 || secondsLeft % 5 == 0) {
                    Player player = this.getPlayer(leader.getLinkInfo());
                    if (player != null) {
                      BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
                      player.sendMessage(
                          locale.get(
                              "match-making.pick.time-left",
                              Maps.builder(
                                      "time",
                                      new Time(secondsLeft, Unit.SECONDS).toEffectiveString())
                                  .append(
                                      "picks", Lots.pretty(this.getParticipantsNames(string)))));
                    }
                  }
                }
              }
            },
            0,
            20);
  }

  /**
   * Get the appropriated team name
   *
   * @param captain the leader of the team
   * @param extra an extra string to add in the team name
   * @return the name of the team
   */
  @NonNull
  public String getTeamName(LinkableInfo captain, @NonNull String extra) {
    String nickname = captain.getIdentification().getOr("nickname", String.class, "");
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
   * @param match the match to get the teams from
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
   * @param matchId the id of the match to get the leaders
   * @return the random leaders
   */
  @NonNull
  public List<TeamMember> pickLeaders(@NonNull String matchId) {
    List<TeamMember> captains = new ArrayList<>();
    Collection<LinkableInfo> playersLeft =
        this.playersLeft.computeIfAbsent(matchId, string -> new ArrayList<>());
    for (LinkableInfo linkableInfo : RandomUtils.getRandom(new ArrayList<>(playersLeft), 2)) {
      captains.add(new SimpleTeamMember(linkableInfo, TeamRole.LEADER));
      playersLeft.remove(linkableInfo);
    }
    return captains;
  }

  /**
   * Get the bukkit player from linked info
   *
   * @param info the info to get the player from
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
    return UUIDUtils.untrim(info.getIdentification().getOr("uuid", String.class, ""));
  }

  /**
   * Get the selecting team from a member
   *
   * @param matchId the id of the match to get the selecting team
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
   * @param matchId the id of the match to get the information for the next pick
   * @param captain the captain picking a player
   * @param info the link info of the player
   * @throws IllegalArgumentException if the captain argument is not a captain or if the captain is
   *     not currently picking
   */
  public void pick(
      @NonNull String matchId, @NonNull TeamMember captain, @NonNull LinkableInfo info) {
    SelectingTeam selecting = this.getSelecting(matchId, captain);
    if (selecting != null && this.isPicking(matchId, captain)) {
      SimpleTeamMember teamMember = new SimpleTeamMember(info, TeamRole.NORMAL);
      selecting.getMembers().add(teamMember);
      PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
      if (listener != null) {
        listener.add(this.getUuid(info), selecting.getParty());
      }
      Collection<LinkableInfo> playersLeft = this.getPlayersLeft(matchId);
      playersLeft.remove(info);
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
    Logger logger = Guido.getLogger();
    Collection<LinkableInfo> playersLeft = this.getPlayersLeft(matchId);
    if (playersLeft.isEmpty()) {
      logger.info("Players left is now empty");
      PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
      if (listener != null) {
        HostedMatch match = listener.getMatch(matchId);
        if (match == null) return;
        Match matchPgm = match.getPgm();
        if (matchPgm == null) return;
        matchPgm
            .needModule(StartMatchModule.class)
            .forceStartCountdown(
                Duration.ofSeconds(PGMMatchMakingListener.secondsToStart), Duration.ZERO);
        for (SelectingTeam selectingTeam : this.getTeams(matchId)) {
          SimpleMatchTeam construct = selectingTeam.construct();
          try {
            Integer id =
                new BukkitIntRequest(
                        "match-add-team", Maps.objects("id", matchId).append("team", construct))
                    .send();
            if (id != null) {
              if (selectingTeam.getParty() instanceof Team) {
                match.getTeams().put(((Team) selectingTeam.getParty()).getId(), id);
              }
            }
          } catch (MessengerListenFailException e) {
            e.printStackTrace();
          }
        }
      }
      this.clear();
    } else if (playersLeft.size() == 2) {
      this.nextLeader(matchId, team.getLeader());
    } else {
      TeamMember leader = this.getNext(matchId, team).getLeader();
      if (playersLeft.size() == 1) {
        this.currentLeader.put(matchId, leader);
        for (LinkableInfo linkableInfo : playersLeft) {
          this.pick(matchId, leader, linkableInfo);
          break;
        }
      } else {
        this.nextLeader(matchId, this.getNext(matchId, team).getLeader());
      }
    }
  }

  /**
   * Set the next leader to pick the next player
   *
   * @param matchId the id of the match to put the seconds left and the leader
   * @param nextLeader the next leader to play the match
   */
  public void nextLeader(@NonNull String matchId, @NonNull TeamMember nextLeader) {
    this.currentLeader.put(matchId, nextLeader);
    this.secondsLeft.put(matchId, PickTeamSelection.timeToPick);
    Player player = this.getPlayer(nextLeader.getLinkInfo());
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
   * @param matchId the id of the match to get the collection of teams
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
   * @param matchId the match to get the collection of currently playing teams
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
   * @param matchId the match id to get the member
   * @param member the member to check if it is picking
   * @return true if it is picking
   */
  public boolean isPicking(@NonNull String matchId, @NonNull TeamMember member) {
    return member.equals(this.currentLeader.get(matchId));
  }

  /**
   * Get the collection of players left to pick
   *
   * @param matchId the id of the match to get the players left
   * @return the players left to pick
   */
  @NonNull
  public Collection<LinkableInfo> getPlayersLeft(@NonNull String matchId) {
    return this.playersLeft.computeIfAbsent(matchId, string -> new ArrayList<>());
  }

  /**
   * Get all the teams that are currently to pick
   *
   * @param matchId the id of the match to get the collection
   * @return the team
   */
  @NonNull
  public List<SelectingTeam> getTeams(@NonNull String matchId) {
    return this.teams.computeIfAbsent(matchId, string -> new ArrayList<>());
  }

  /**
   * Return the nicknames of the participants of the match
   *
   * @param matchId the match to match and get the players left
   * @return the names of the participants
   */
  @NonNull
  public List<String> getParticipantsNames(@NonNull String matchId) {
    List<String> names = new ArrayList<>();
    for (LinkableInfo linkableInfo : this.getPlayersLeft(matchId)) {
      String nickname =
          linkableInfo
              .getIdentification()
              .get("nickname", String.class); // Nickname is no longer supplied
      if (nickname != null) {
        names.add(nickname);
      }
    }
    return names;
  }

  /**
   * Get the seconds left that a leader has
   *
   * @param matchId the match id to get the leader
   * @return the seconds left of the leader of the match
   */
  public long getSecondsLeft(@NonNull String matchId) {
    return this.secondsLeft.computeIfAbsent(matchId, string -> PickTeamSelection.timeToPick);
  }

  /** Clears the team creator */
  @Override
  public void clear() {
    this.playersLeft.clear();
    this.teams.clear();
    this.currentLeader.clear();
    this.secondsLeft.clear();
  }

  @Override
  public void createTeams(
      @NonNull PGMMatchMakingListener matchMaking,
      @NonNull HostedMatch hostedMatch,
      @NonNull Match match) {
    this.playersLeft.put(hostedMatch.getId(), hostedMatch.getParticipants());
    List<TeamMember> leaders = this.pickLeaders(hostedMatch.getId());
    for (TeamMember leader : leaders) {
      Team teamParty = this.getParty(hostedMatch.getId(), match);
      Player player = this.getPlayer(leader.getLinkInfo());
      String teamName =
          this.getTeamName(leader.getLinkInfo(), String.valueOf(leaders.indexOf(leader) + 1));
      this.teams
          .computeIfAbsent(hostedMatch.getId(), string -> new ArrayList<>())
          .add(new SelectingTeam(teamParty, leader));
      teamParty.setName(teamName);
      matchMaking.add(this.getUuid(leader.getLinkInfo()), teamParty);
      if (player != null) {
        BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
        player.sendMessage(locale.get("match-making.pick.leader"));
      }
    }
    this.currentLeader.put(hostedMatch.getId(), RandomUtils.getRandom(leaders));
  }

  /**
   * This object represents a team which is going to play the match but it is still selecting
   * players
   */
  public class SelectingTeam {

    /** The pgm party assigned to this team */
    @NonNull private final Party party;

    /** The leader of the team */
    @NonNull private final TeamMember leader;

    /** The member of the team */
    @NonNull private final Collection<TeamMember> members = new HashSet<>();

    /**
     * Create the team
     *
     * @param party the pgm party assigned to this team
     * @param leader the leader which is selecting players
     */
    SelectingTeam(@NonNull Party party, @NonNull TeamMember leader) {
      this.party = party;
      this.leader = leader;
    }

    /**
     * Create the team to make requests to the bot
     *
     * @return the constructed team
     */
    public SimpleMatchTeam construct() {
      Set<TeamMember> copy = new HashSet<>(this.members);
      copy.add(this.leader);
      return new SimpleMatchTeam(-3, this.party.getNameLegacy(), copy);
    }

    /**
     * Get the pgm party associated with this team
     *
     * @return the pgm party
     */
    @NonNull
    public Party getParty() {
      return this.party;
    }

    /**
     * Get the leader which is selecting members
     *
     * @return the leader selecting members
     */
    @NonNull
    public TeamMember getLeader() {
      return this.leader;
    }

    /**
     * Get the members of the team
     *
     * @return the members of the team
     */
    @NonNull
    public Collection<TeamMember> getMembers() {
      return this.members;
    }

    /**
     * Get the uuid of the team leader
     *
     * @return the uuid
     */
    @NonNull
    public UUID getLeaderUniqueId() {
      return PickTeamSelection.this.getUuid(this.leader.getLinkInfo());
    }
  }
}
