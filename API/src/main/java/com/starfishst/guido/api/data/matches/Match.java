package com.starfishst.guido.api.data.matches;

import com.starfishst.guido.api.data.ValuesMap;
import java.util.Collection;
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
  Collection<? extends Team> getTeams();

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
  MatchStatus getStatus();
}
