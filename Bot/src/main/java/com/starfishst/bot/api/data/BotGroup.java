package com.starfishst.bot.api.data;

import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.guido.api.data.Group;
import org.jetbrains.annotations.NotNull;

/** An extension of group for the bot */
public interface BotGroup extends Group<GuidoPermission, GuidoPermissionStack>, BotPermissible {

  @Override
  @NotNull
  BotGroup refresh();
}
