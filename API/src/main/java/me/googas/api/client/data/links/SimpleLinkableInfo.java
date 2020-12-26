package me.googas.api.client.data.links;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.builder.ToStringBuilder;

public class SimpleLinkableInfo implements LinkableInfo {

  @NonNull private final LinkableType type;
  @NonNull private final ValuesMap identification;

  /**
   * Create the link information
   *
   * @param type the type of link
   * @param identification the way to identify the link
   */
  public SimpleLinkableInfo(@NonNull LinkableType type, @NonNull SimpleValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  @Override
  public @NonNull Map<String, Float> getStats() {
    return new HashMap<>();
  }

  @Override
  public @NonNull String getSingle() {
    Linkable link = this.getLink();
    return link == null ? "invalid" : link.getSingle();
  }

  @Override
  public @NonNull LinkableType getType() {
    return this.type;
  }

  @Override
  public @NonNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public Linkable getLink() {
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
