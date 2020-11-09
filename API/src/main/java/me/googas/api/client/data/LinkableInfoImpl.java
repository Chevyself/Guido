package me.googas.api.client.data;

import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableDataType;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for linked info */
public class LinkableInfoImpl implements LinkableInfo {

  /** The type of the link */
  @NotNull private final LinkableDataType type;

  /** The way to identify the link */
  @NotNull private final ValuesMap identification;

  /**
   * Create the link information
   *
   * @param type the type of link
   * @param identification the way to identify the link
   */
  public LinkableInfoImpl(@NotNull LinkableDataType type, @NotNull ValuesMapImpl identification) {
    this.type = type;
    this.identification = identification;
  }

  @Override
  public @NotNull LinkableDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public @Nullable LinkableData getLink() {
    return null;
  }

  @Override
  public String toString() {
    return "LinkedInfo{" + "type=" + this.type + ", identification=" + this.identification + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LinkableInfoImpl)) return false;

    LinkableInfoImpl that = (LinkableInfoImpl) o;

    if (this.type != that.type) return false;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }
}
