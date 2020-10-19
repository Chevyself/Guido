package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.guido.api.data.links.LinkedDataType;
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
}
