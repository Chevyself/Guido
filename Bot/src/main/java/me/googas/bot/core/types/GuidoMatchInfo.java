package me.googas.bot.core.types;

import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.bot.core.Guido;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Implementation for match info */
public class GuidoMatchInfo implements MatchInfo {

  /** The id of the match */
  @NotNull private final String id;

  /** The id of the guild where the match is happening */
  private final long guildId;

  /**
   * Create the information of the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match was played
   */
  public GuidoMatchInfo(@NotNull String id, long guildId) {
    this.id = id;
    this.guildId = guildId;
  }

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @Override
  public @NotNull String getId() {
    return this.id;
  }

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  @Override
  public long getGuildId() {
    return this.guildId;
  }

  /**
   * Get the match with the provided information by this object
   *
   * @return the match if found null otherwise
   */
  @Override
  public @Nullable Match getMatch() {
    return Guido.getDataLoader().getMatch(this.id);
  }
}
