package me.googas.bot.core.matches;

import java.util.Objects;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.bot.api.Guido;
import me.googas.commons.builder.ToStringBuilder;

/** Implementation for match info */
public class GuidoMatchInfo implements MatchInfo {

  @NonNull private final String id;
  private final long guildId;

  /**
   * Create the information of the match
   *
   * @param id the id of the match
   * @param guildId the id of the guild where the match was played
   */
  public GuidoMatchInfo(@NonNull String id, long guildId) {
    this.id = id;
    this.guildId = guildId;
  }

  @Nullable
  @Override
  public Match toMatch() {
    return Guido.getHandlers().getLoader().getMatches().getMatch(this.id);
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public Match getMatch() {
    return Guido.getHandlers().getLoader().getMatches().getMatch(this.id);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("id", this.id).append("guildId", this.guildId).build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoMatchInfo that = (GuidoMatchInfo) o;
    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
