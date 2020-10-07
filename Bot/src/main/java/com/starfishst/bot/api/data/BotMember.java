package com.starfishst.bot.api.data;

import com.starfishst.bot.api.events.data.member.BotMemberNewLinkEvent;
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

  @Override
  default void addLink(@NotNull String key, @NotNull String value) {
    new BotMemberNewLinkEvent(this, key, value).call();
  }
}
