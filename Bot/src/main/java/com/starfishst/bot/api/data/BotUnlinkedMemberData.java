package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.UnlinkedMemberData;
import org.jetbrains.annotations.NotNull;

/** An unlinked member implementation */
public interface BotUnlinkedMemberData extends UnlinkedMemberData, BotMember {

  @Override
  default void addLink(@NotNull String key, @NotNull String value) {
    throw new UnsupportedOperationException("Unlinked members cannot have links");
  }
}
