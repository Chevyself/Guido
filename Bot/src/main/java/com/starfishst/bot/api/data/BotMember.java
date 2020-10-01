package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.MemberData;
import org.jetbrains.annotations.NotNull;

/** A member used in discord */
public interface BotMember extends MemberData, BotPermissible {

  /**
   * Create a copy of this same instance
   *
   * @return the copy of this instance
   */
  @NotNull
  BotMember copy();
}
