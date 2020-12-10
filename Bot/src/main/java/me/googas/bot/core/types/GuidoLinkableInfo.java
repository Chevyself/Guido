package me.googas.bot.core.types;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.links.LinkableType;
import me.googas.bot.api.types.BotLinkable;
import me.googas.bot.api.types.BotLinkableInfo;
import me.googas.bot.core.types.maps.GuidoValuesMap;

/** The uncompleted data from a linked data */
public class GuidoLinkableInfo implements BotLinkableInfo {

  /** The type of linked data */
  @NonNull private final LinkableType type;

  /** The way to identify this data */
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

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @Override
  public @NonNull Map<String, Float> getStats() {
    BotLinkable link = this.getLink();
    return link == null ? new HashMap<>() : link.getStats();
  }

  /**
   * Get as a single way to identify it. For example in the case of discord it will be the tag, for
   * minecraft its nickname and if its a team it's name
   *
   * @return a simple way to identify the data
   */
  @Override
  public @NonNull String getSingle() {
    BotLinkable link = this.getLink();
    return link == null ? "invalid" : link.getSingle();
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
    return "GuidoLinkedInfo{"
        + "type="
        + this.type
        + ", identification="
        + this.identification
        + '}';
  }
}
