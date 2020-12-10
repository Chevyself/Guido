package me.googas.api.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.RandomUtils;
import me.googas.commons.cache.Catchable;

/** This object represents a match which was played by one ore more teams */
public interface Match extends Catchable {

  /**
   * Get whether the given information is inside a team of this match
   *
   * @param type the type of link
   * @param identification the way to identify it
   * @return true if it matches a member from a team
   */
  default boolean isParticipating(@NonNull LinkableType type, @NonNull ValuesMap identification) {
    Collection<Team> teams = this.getTeams();
    for (Team team : teams) {
      for (TeamMember member : team.getMembers()) {
        if (member.getLinkInfo().compare(type, identification)) return true;
      }
    }
    return false;
  }

  /**
   * Finishes the match
   *
   * @param winners the winners of the match
   */
  default void finish(Team winners) {
    this.setWinners(winners);
    this.setStatus(MatchStatus.FINISHED);
  }

  /**
   * Get a team that is playing this match by its name
   *
   * @param name the name of the team to get
   * @return the team if the name matches else null
   */
  default Team getTeam(@NonNull String name) {
    for (Team team : this.getTeams()) {
      if (team.getName().equalsIgnoreCase(name)) {
        return team;
      }
    }
    return null;
  }

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  long getGuildId();

  /**
   * Add a team to this match
   *
   * @param team the team to add
   * @return whether the team was added
   */
  boolean addTeam(@NonNull Team team);

  /**
   * Removes a team from this match
   *
   * @param team the team to remove
   * @return whether the team was removed
   */
  boolean removeTeam(@NonNull Team team);

  /**
   * Get the team that won the match
   *
   * @return the team that won the match. This can return null in case that the match has not
   *     finished yet
   */
  Team getWinners();

  /**
   * Get the next unused id for a team
   *
   * @return the unused team id
   */
  default int nextTeamId() {
    int i = RandomUtils.getRandom().nextInt();
    Team given = this.getTeam(i);
    if (given == null) {
      return i;
    } else {
      return this.nextTeamId();
    }
  }

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  void setWinners(Team winners);

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  void setStatus(@NonNull MatchStatus status);

  /**
   * Get the ladder in which the match was played
   *
   * @return the ladder where the match was played
   */
  Ladder getLadder();

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @NonNull
  String getId();

  /**
   * Get the teams that are participating in the match
   *
   * @return collection of teams
   */
  @NonNull
  Collection<Team> getTeams();

  /**
   * Get a team that is playing this match by its id
   *
   * @param id the id of the team to get
   * @return the team if the id matches else null
   */
  default Team getTeam(int id) {
    for (Team team : this.getTeams()) {
      if (team.getId() == id) {
        return team;
      }
    }
    return null;
  }

  /**
   * Get the details of the match
   *
   * @return the details of the match
   */
  @NonNull
  ValuesMap getDetails();

  /**
   * Get the status of the match
   *
   * @return the status of the match
   */
  @NonNull
  MatchStatus getStatus();

  /**
   * Get the participants of a match. This collection must be immutable
   *
   * @return the collection of participants
   */
  @NonNull
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
   * Get the data of the guild in which this match is being played
   *
   * @return the guild data of the guild of this match
   */
  GuildData getGuild();
}
