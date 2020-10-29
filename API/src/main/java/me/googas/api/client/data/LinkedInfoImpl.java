package me.googas.api.client.data;

import me.googas.api.ValuesMap;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for linked info */
public class LinkedInfoImpl implements LinkedInfo {

  /** The type of the link */
  @NotNull private final LinkedDataType type;

  /** The way to identify the link */
  @NotNull private final ValuesMap identification;

  /**
   * Create the link information
   *
   * @param type the type of link
   * @param identification the way to identify the link
   */
  public LinkedInfoImpl(@NotNull LinkedDataType type, @NotNull ValuesMapImpl identification) {
    this.type = type;
    this.identification = identification;
  }

  @Override
  public @NotNull LinkedDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public @Nullable LinkedData getLink() {
    return null;
  }

  @Override
  public String toString() {
    return "LinkedInfo{" + "type=" + this.type + ", identification=" + this.identification + '}';
  }
}
