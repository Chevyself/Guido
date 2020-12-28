package me.googas.bot.core.links;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;

/** The uncompleted data from a linked data */
public class GuidoLinkableInfo implements LinkableInfo {

  /** The version of serialization for the scheme */
  @NonNull @Getter private final String version = "PRE-3";

  @NonNull private final LinkableType type;
  @NonNull private final GuidoValuesMap identification;

  /**
   * Create the uncompleted data
   *
   * @param type the type of data
   * @param identification the way to identify the data
   */
  public GuidoLinkableInfo(@NonNull LinkableType type, @NonNull GuidoValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkableInfo() {
    this(LinkableType.NONE, new GuidoValuesMap());
  }

  @Override
  public @NonNull Map<String, Float> getStats() {
    Linkable link = this.getLink();
    return link == null ? new HashMap<>() : link.getStats();
  }

  @Override
  public @NonNull String getSingle() {
    Linkable link = this.getLink();
    return link == null ? "invalid" : link.getSingle();
  }

  @Override
  public Linkable getLink() {
    return Guido.getDataLoader().getLink(this.getType(), this.getIdentification());
  }

  @Override
  public @NonNull LinkableType getType() {
    return this.type;
  }

  @Override
  public @NonNull GuidoValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof GuidoLinkableInfo)) return false;
    GuidoLinkableInfo that = (GuidoLinkableInfo) object;
    return this.compare(that);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", this.type)
        .append("identification", this.identification)
        .build();
  }
}
