package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.guido.api.data.links.LinkedData;
import org.jetbrains.annotations.Nullable;

/** An extension for linked data for bot use */
public interface BotLinkedData extends LinkedData {

  @Override
  @Nullable
  BotUser getLinkedUser();
}
