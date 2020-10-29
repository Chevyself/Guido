package me.googas.api.matches;

import me.googas.api.ValuesMap;
import me.googas.api.links.LinkedInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents a match which was played by one ore more teams */
public interface Match extends ICatchable {

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
  default Collection<LinkedInfo> getParticipants() {
    Set<LinkedInfo> participants = new HashSet<>();
    for (Team team : this.getTeams()) {
      for (TeamMember member : team.getMembers()) {
        participants.add(member.getLinkInfo());
      }
    }
    return Collections.unmodifiableSet(participants);
  }

  @Override
  @NotNull
  Match refresh();
}
