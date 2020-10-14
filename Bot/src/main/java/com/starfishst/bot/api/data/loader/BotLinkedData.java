package com.starfishst.bot.api.data.loader;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.links.LinkedData;
import org.jetbrains.annotations.Nullable;

/** An extension for linked data for bot use */
public interface BotLinkedData extends LinkedData<GuidoPermission> {

  @Override
  @Nullable
  BotUser getLinkedUser();
}
