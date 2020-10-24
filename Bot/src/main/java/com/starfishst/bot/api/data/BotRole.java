package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.discord.RoleData;
import org.jetbrains.annotations.NotNull;

/** A role data for the bot */
public interface BotRole extends RoleData, BotPermissible {

  @Override
  @NotNull
  BotRole refresh();
}
