package com.starfishst.bot.handlers.data.types;

import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.handlers.data.types.maps.GuidoValuesMap;
import me.googas.api.links.LinkedDataType;
import org.jetbrains.annotations.NotNull;

/** The uncompleted data from a linked data */
public class GuidoLinkedInfo implements BotLinkedInfo {

  /** The type of linked data */
  @NotNull private final LinkedDataType type;

  /** The way to identify this data */
  @NotNull private final GuidoValuesMap identification;

  /**
   * Create the uncompleted data
   *
   * @param type the type of data
   * @param identification the way to identify the data
   */
  public GuidoLinkedInfo(@NotNull LinkedDataType type, @NotNull GuidoValuesMap identification) {
    this.type = type;
    this.identification = identification;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkedInfo() {
    this(LinkedDataType.NONE, new GuidoValuesMap());
  }

  @Override
  public @NotNull LinkedDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull GuidoValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof GuidoLinkedInfo)) return false;

    GuidoLinkedInfo that = (GuidoLinkedInfo) object;

    if (this.type != that.type) return false;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "GuidoLinkedInfo{" + "type=" + this.type + ", identification=" + this.identification + '}';
  }
}
