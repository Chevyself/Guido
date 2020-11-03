package com.starfishst.bukkit.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.client.BukkitBooleanRequest;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.listeners.matches.MatchMakingListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.googas.api.client.data.TeamImpl;
import me.googas.api.client.data.TeamMemberImpl;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Select a teammate by picking it */
public class PickTeamSelection implements TeamCreation {

  /**
   * The time for a leader to pick
   */
  private static final long timeToPick = Time.fromString("1m").getValue(Unit.SECONDS);

  /** The players left to select */
  @NotNull private final Collection<LinkedInfo> playersLeft = new HashSet<>();

  /** The teams that were selected */
  @NotNull private final List<SelectingTeam> teams = new ArrayList<>();

  /**
   * The current leader picking players
   */
  @Nullable
  private TeamMember currentLeader;

  /**
   * The time left for the leader to pick
   */
  private long secondsLeft = PickTeamSelection.timeToPick;

  /**
   * Create the team selection by picking the players
   */
  public PickTeamSelection() {
    Bukkit.getScheduler()
        .runTaskTimer(
            Guido.validated(),
            () -> {
              if (this.currentLeader != null) {
                this.secondsLeft--;
                if (this.secondsLeft < 1) {
                  this.pick(this.currentLeader, RandomUtils.getRandom(new ArrayList<>(this.playersLeft)));
                } else {
                  if (this.secondsLeft <= 10 || this.secondsLeft % 5 == 0) {
                    Player player = this.getPlayer(this.currentLeader.getLinkInfo());
                    if (player != null) {
                      BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
                      player.sendMessage(
                              locale.get(
                                      "match-making.pick.time-left",
                                      Maps.singleton(
                                              "time",
                                              new Time(this.secondsLeft, Unit.SECONDS).toEffectiveString())));
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
  @NotNull
  public String getTeamName(LinkedInfo captain, @NotNull String extra) {
    String nickname = captain.getIdentification().getValueOr("nickname", String.class, "");
    if (!nickname.isEmpty()) {
      return nickname + "'s team";
    } else {
      return "Team " + extra;
    }
  }

  /**
   * Get the pgm team that the team can use
   *
   * @param match the match to get the teams from
   * @return the team
   * @throws IllegalStateException if there's no team available which means that
   * the map picked was incorrect
   */
  @NotNull
  public Team getParty(@NotNull Match match) {
    Party observers = match.getDefaultParty();
    for (Party party : match.getParties()) {
      if (party != observers && !this.isOccupied(party) && party instanceof Team) {
        return (Team) party;
      }
    }
    throw new IllegalStateException("There was no appropriated amount of teams to give one next");
  }

  /**
   * Pick two random leaders
   *
   * @return the random leaders
   */
  @NotNull
  public List<TeamMember> pickLeaders() {
    List<TeamMember> captains = new ArrayList<>();
    for (LinkedInfo linkedInfo : RandomUtils.getRandom(new ArrayList<>(this.playersLeft), 2)) {
      captains.add(new TeamMemberImpl(linkedInfo, TeamRole.LEADER));
      this.playersLeft.remove(linkedInfo);
    }
    return captains;
  }

  /**
   * Get the bukkit player from linked info
   *
   * @param info the info to get the player from
   * @return the player but if not online it will be null
   */
  @Nullable
  public Player getPlayer(@NotNull LinkedInfo info) {
    return Bukkit.getPlayer(this.getUuid(info));
  }

  /**
   * Get the uuid from the linked information
   *
   * @param info the linked information
   * @return the uuid
   */
  @NotNull
  public UUID getUuid(@NotNull LinkedInfo info) {
    return UUIDUtils.untrim(info.getIdentification().getValueOr("uuid", String.class, ""));
  }

  /**
   * Get the selecting team from a member
   *
   * @param captain the captain of the team
   * @return the team if the given team member is a captain from a team
   */
  @Nullable
  private SelectingTeam getSelecting(@NotNull TeamMember captain) {
    for (SelectingTeam team : this.teams) {
      if (team.getLeader().equals(captain)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Make the captain pick a player
   *
   * @param captain the captain picking a player
   * @param info the link info of the player
   * @throws IllegalArgumentException if the captain argument is not a captain or if the
   * captain is not currently picking
   */
  public void pick(@NotNull TeamMember captain, @NotNull LinkedInfo info) {
    SelectingTeam selecting = this.getSelecting(captain);
    if (selecting != null && this.isPicking(captain)) {
      TeamMemberImpl teamMember = new TeamMemberImpl(info, TeamRole.NORMAL);
      selecting.getMembers().add(teamMember);
      MatchMakingListener listener = Guido.getListener(MatchMakingListener.class);
      if (listener != null) {
        listener.add(this.getUuid(info), selecting.getParty());
      }
      this.playersLeft.remove(info);
      this.nextPick(selecting);
    } else {
      if (selecting == null) {
        throw new IllegalArgumentException(captain + " is not a captain of a team");
      }
      if (!this.isPicking(captain)) {
        throw new IllegalArgumentException(captain + " is not currently picking");
      }
    }
  }

  /**
   * Sets the next captain that is picking the match or starts the match in case there's no
   * players left
   *
   * @param team the team that just selected a player
   */
  public void nextPick(@NotNull SelectingTeam team) {
    if (this.playersLeft.isEmpty()) {
      MatchMakingListener listener = Guido.getListener(MatchMakingListener.class);
      if (listener != null) {
        this.teams
            .get(0)
            .getParty()
            .getMatch()
            .needModule(StartMatchModule.class)
            .forceStartCountdown(Duration.ofSeconds(120), Duration.ZERO);
        for (SelectingTeam selectingTeam : this.teams) {
          new BukkitBooleanRequest(
              "match-add-team",
              Maps.objects("id", listener.getMatchId()).append("team", selectingTeam.construct()));
        }
      }
      this.clear();
    } else if (this.playersLeft.size() == 2) {
      this.currentLeader = team.getLeader();
      this.secondsLeft = PickTeamSelection.timeToPick;
    } else if (this.playersLeft.size() == 1) {
      for (LinkedInfo linkedInfo : this.getPlayersLeft()) {
        this.pick(this.getNext(team).getLeader(), linkedInfo);
        break;
      }
    } else {
      this.currentLeader = this.getNext(team).getLeader();
      this.secondsLeft = PickTeamSelection.timeToPick;
    }
  }

  /**
   * Get the next team that has to pick
   *
   * @param team the current team picking
   * @return the next team to pick
   */
  @NotNull
  public SelectingTeam getNext(@NotNull SelectingTeam team) {
    if (this.teams.indexOf(team) == this.teams.size() - 1) {
      return this.teams.get(0);
    } else {
      return this.teams.get(this.teams.indexOf(team) + 1);
    }
  }

  /**
   * Checks whether a pgm party is already occupied by another team
   *
   * @param party the party to check
   * @return true if there's a team assigned to the party else null
   */
  private boolean isOccupied(@NotNull Party party) {
    for (SelectingTeam team : this.teams) {
      if (team.getParty().equals(party)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a team member is currently picking
   *
   * @param member the member to check if it is picking
   * @return true if it is picking
   */
  public boolean isPicking(@NotNull TeamMember member) {
    return member.equals(this.currentLeader);
  }

  /**
   * Get the collection of players left to pick
   *
   * @return the players left to pick
   */
  @NotNull
  public Collection<LinkedInfo> getPlayersLeft() {
    return this.playersLeft;
  }

  /**
   * Get all the teams that are currently to pick
   *
   * @return the team
   */
  @NotNull
  public Collection<SelectingTeam> getTeams() {
    return this.teams;
  }

  @Override
  public void createTeams(@NotNull MatchMakingListener matchMaking, @NotNull Match match) {
    this.playersLeft.addAll(matchMaking.getParticipants());
    List<TeamMember> leaders = this.pickLeaders();
    for (TeamMember leader : leaders) {
      Team teamParty = this.getParty(match);
      Player player = this.getPlayer(leader.getLinkInfo());
      String teamName = this.getTeamName(leader.getLinkInfo(), String.valueOf(leaders.indexOf(leader) + 1));
      this.teams.add(new SelectingTeam(teamParty, leader));
      teamParty.setName(teamName);
      matchMaking.add(this.getUuid(leader.getLinkInfo()), teamParty);
      if (player != null) {
        BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(player);
        player.sendMessage(locale.get("match-making.pick.leader"));
      }
    }
    this.currentLeader = RandomUtils.getRandom(leaders);
  }

  /** Clears the team creator */
  @Override
  public void clear() {
    this.playersLeft.clear();
    this.teams.clear();
    this.currentLeader = null;
  }

  /**
   * This object represents a team which is going to play the match but it is still selecting players
   */
  public class SelectingTeam {

    /**
     * The pgm party assigned to this team
     */
    @NotNull private final Party party;

    /**
     * The leader of the team
     */
    @NotNull private final TeamMember leader;

    /**
     * The member of the team
     */
    @NotNull private final Collection<TeamMember> members = new HashSet<>();

    /**
     * Create the team
     *
     * @param party the pgm party assigned to this team
     * @param leader the leader which is selecting players
     */
    SelectingTeam(@NotNull Party party, @NotNull TeamMember leader) {
      this.party = party;
      this.leader = leader;
    }

    /**
     * Create the team to make requests to the bot
     *
     * @return the constructed team
     */
    public TeamImpl construct() {
      Set<TeamMember> copy = new HashSet<>(this.members);
      copy.add(this.leader);
      return new TeamImpl(-3, this.party.getNameLegacy(), copy);
    }

    /**
     * Get the pgm party associated with this team
     *
     * @return the pgm party
     */
    @NotNull
    public Party getParty() {
      return this.party;
    }

    /**
     * Get the leader which is selecting members
     *
     * @return the leader selecting members
     */
    @NotNull
    public TeamMember getLeader() {
      return this.leader;
    }

    /**
     * Get the members of the team
     *
     * @return the members of the team
     */
    @NotNull
    public Collection<TeamMember> getMembers() {
      return this.members;
    }

    /**
     * Get the uuid of the team leader
     *
     * @return the uuid
     */
    @NotNull
    public UUID getLeaderUniqueId() {
      return PickTeamSelection.this.getUuid(this.leader.getLinkInfo());
    }
  }
}
