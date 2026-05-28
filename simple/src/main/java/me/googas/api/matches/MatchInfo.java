package me.googas.api.matches;

import lombok.NonNull;
import me.googas.api.API;

/** This object represents the information of a match */
public class MatchInfo {

  @NonNull private final String id;

  /**
   * Create the match information
   *
   * @param id the id of the match
   */
  public MatchInfo(@NonNull String id) {
    this.id = id;
  }

  /** @deprecated this constructor may only be used by gson */
  public MatchInfo() {
    this("");
  }

  /**
   * Get the match information as a match
   *
   * @deprecated use {@link #getMatch()}
   * @return the match
   */
  public AbstractMatch toMatch() {
    return this.getMatch();
  }

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  @Deprecated
  public long getGuildId() {
    return -1;
  }

  /**
   * Get the match with the provided information by this object
   *
   * @return the match if found null otherwise
   */
  public AbstractMatch getMatch() {
    return API.getLoader().getMatches().getMatch(this.id);
  }
}
