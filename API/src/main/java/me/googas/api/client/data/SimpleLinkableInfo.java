package me.googas.api.client.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for linked info */
public class SimpleLinkableInfo implements LinkableInfo {

  /** The type of the link */
  @NotNull private final LinkableType type;

  /** The way to identify the link */
  @NotNull private final ValuesMap identification;

  /**
   * Create the link information
   *
   * @param type the type of link
   * @param identification the way to identify the link
   */
  public SimpleLinkableInfo(@NotNull LinkableType type, @NotNull SimpleValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  @Override
  public @NotNull Map<String, Float> getStats() {
    return new HashMap<>();
  }

  /**
   * Get as a single way to identify it. For example in the case of discord it will be the tag, for
   * minecraft its nickname and if its a team it's name
   *
   * @return a simple way to identify the data
   */
  @Override
  public @NotNull String getSingle() {
    Linkable link = this.getLink();
    return link == null ? "invalid" : link.getSingle();
  }

  @Override
  public @NotNull LinkableType getType() {
    return this.type;
  }

  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public @Nullable Linkable getLink() {
    return null;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", this.type)
        .append("identification", this.identification)
        .build();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.identification);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimpleLinkableInfo that = (SimpleLinkableInfo) object;
    return this.type == that.type && this.identification.equals(that.identification);
  }
}
