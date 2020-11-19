package me.googas.api.matches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents the information of a match */
public interface MatchInfo {

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
   * Get the match with the provided information by this object
   *
   * @return the match if found null otherwise
   */
  @Nullable
  Match getMatch();
}
