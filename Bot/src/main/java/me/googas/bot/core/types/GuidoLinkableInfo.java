package me.googas.bot.core.types;

import me.googas.api.links.LinkableDataType;
import me.googas.bot.api.types.BotLinkableInfo;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import org.jetbrains.annotations.NotNull;

/** The uncompleted data from a linked data */
public class GuidoLinkableInfo implements BotLinkableInfo {

  /** The type of linked data */
  @NotNull private final LinkableDataType type;

  /** The way to identify this data */
  @NotNull private final GuidoValuesMap identification;

  /**
   * Create the uncompleted data
   *
   * @param type the type of data
   * @param identification the way to identify the data
   */
  public GuidoLinkableInfo(@NotNull LinkableDataType type, @NotNull GuidoValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkableInfo() {
    this(LinkableDataType.NONE, new GuidoValuesMap());
  }

  @Override
  public @NotNull LinkableDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull GuidoValuesMap getIdentification() {
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
