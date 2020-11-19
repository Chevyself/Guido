package me.googas.api.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.discord.GuildData;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.RandomUtils;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents a match which was played by one ore more teams */
public interface Match extends Catchable {

  /**
   * Get whether the given information is inside a team of this match
   *
   * @param type the type of link
   * @param identification the way to identify it
   * @return true if it matches a member from a team
   */
  default boolean isParticipating(@NotNull LinkableType type, @NotNull ValuesMap identification) {
    Collection<Team> teams = this.getTeams();
    for (Team team : teams) {
      for (TeamMember member : team.getMembers()) {
        if (member.getLinkInfo().compare(type, identification)) return true;
      }
    }
    return false;
  }

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @NotNull
  String getId();

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  long getGuildId();

  /**
   * Finishes the match
   *
   * @param winners the winners of the match
   */
  default void finish(@Nullable Team winners) {
    this.setWinners(winners);
    this.setStatus(MatchStatus.FINISHED);
  }

  /**
   * Get the teams that are participating in the match
   *
   * @return collection of teams
   */
  @NotNull
  Collection<Team> getTeams();

  /**
   * Get the team that won the match
   *
   * @return the team that won the match. This can return null in case that the match has not
   *     finished yet
   */
  @Nullable
  Team getWinners();

  /**
   * Get the details of the match
   *
   * @return the details of the match
   */
  @NotNull
  ValuesMap getDetails();

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  void setWinners(@Nullable Team winners);

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  void setStatus(@NotNull MatchStatus status);

  /**
   * Get the status of the match
   *
   * @return the status of the match
   */
  @NotNull
  MatchStatus getStatus();

  /**
   * Get the participants of a match. This collection must be immutable
   *
   * @return the collection of participants
   */
  @NotNull
  default Collection<LinkableInfo> getParticipants() {
    Set<LinkableInfo> participants = new HashSet<>();
    for (Team team : this.getTeams()) {
      for (TeamMember member : team.getMembers()) {
        participants.add(member.getLinkInfo());
      }
    }
    return Collections.unmodifiableSet(participants);
  }

  /**
   * Get a team that is playing this match by its name
   *
   * @param name the name of the team to get
   * @return the team if the name matches else null
   */
  @Nullable
  default Team getTeam(@NotNull String name) {
    for (Team team : this.getTeams()) {
      if (team.getName().equalsIgnoreCase(name)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Get a team that is playing this match by its id
   *
   * @param id the id of the team to get
   * @return the team if the id matches else null
   */
  @Nullable
  default Team getTeam(int id) {
    for (Team team : this.getTeams()) {
      if (team.getId() == id) {
        return team;
      }
    }
    return null;
  }

  /**
   * Add a team to this match
   *
   * @param team the team to add
   * @return whether the team was added
   */
  boolean addTeam(@NotNull Team team);

  /**
   * Removes a team from this match
   *
   * @param team the team to remove
   * @return whether the team was removed
   */
  boolean removeTeam(@NotNull Team team);

  /**
   * Get the next unused id for a team
   *
   * @return the unused team id
   */
  default int nextTeamId() {
    int id = RandomUtils.nextInt(0, 20);
    Team team = this.getTeam(id);
    while (team != null) {
      id = RandomUtils.nextInt(0, 20);
      team = this.getTeam(id);
    }
    return id;
  }

  /**
   * Get the data of the guild in which this match is being played
   *
   * @return the guild data of the guild of this match
   */
  @Nullable
  GuildData getGuildData();
}
