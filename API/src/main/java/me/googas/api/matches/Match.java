package me.googas.api.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
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
    Collection<MatchTeam> matchTeams = this.getTeams();
    for (MatchTeam matchTeam : matchTeams) {
      for (TeamMember member : matchTeam.getMembers()) {
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
  default void finish(MatchTeam winners) {
    this.setWinners(winners);
    this.setStatus(MatchStatus.FINISHED);
  }

  /**
   * Get a team that is playing this match by its name
   *
   * @param name the name of the team to get
   * @return the team if the name matches else null
   */
  default MatchTeam getTeam(@NonNull String name) {
    for (MatchTeam matchTeam : this.getTeams()) {
      if (matchTeam.getName().equalsIgnoreCase(name)) {
        return matchTeam;
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
   * Add a matchTeam to this match
   *
   * @param matchTeam the matchTeam to add
   * @return whether the matchTeam was added
   */
  boolean addTeam(@NonNull MatchTeam matchTeam);

  /**
   * Removes a matchTeam from this match
   *
   * @param matchTeam the matchTeam to remove
   * @return whether the matchTeam was removed
   */
  boolean removeTeam(@NonNull MatchTeam matchTeam);

  /**
   * Get the next unused id for a team
   *
   * @return the unused team id
   */
  default int nextTeamId() {
    int i = RandomUtils.getRandom().nextInt();
    MatchTeam given = this.getTeam(i);
    if (given == null) {
      return i;
    } else {
      return this.nextTeamId();
    }
  }

  /**
   * Get a team that is playing this match by its id
   *
   * @param id the id of the team to get
   * @return the team if the id matches else null
   */
  default MatchTeam getTeam(int id) {
    for (MatchTeam matchTeam : this.getTeams()) {
      if (matchTeam.getId() == id) {
        return matchTeam;
      }
    }
    return null;
  }

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  void setWinners(MatchTeam winners);

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
  @Nullable
  Ladder getLadder();

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @NonNull
  String getId();

  /**
   * Get the team that won the match
   *
   * @return the team that won the match. This can return null in case that the match has not
   *     finished yet
   */
  MatchTeam getWinners();

  /**
   * Get the teams that are participating in the match
   *
   * @return collection of teams
   */
  @NonNull
  Collection<MatchTeam> getTeams();

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
    for (MatchTeam matchTeam : this.getTeams()) {
      for (TeamMember member : matchTeam.getMembers()) {
        participants.add(member.getLinkInfo());
      }
    }
    return Collections.unmodifiableSet(participants);
  }
}
