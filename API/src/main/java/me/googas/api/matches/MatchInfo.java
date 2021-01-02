package me.googas.api.matches;

import lombok.NonNull;
import me.googas.annotations.Nullable;

/** This object represents the information of a match */
public interface MatchInfo {

  /**
   * Get the match information as a match
   *
   * @return the match
   */
  @Nullable
  Match toMatch();

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @NonNull
  String getId();

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  long getGuildId();

  /**
   * Get the match with the provided information by this object
   *
   * @return the match if found null otherwise
   */
  Match getMatch();
}
