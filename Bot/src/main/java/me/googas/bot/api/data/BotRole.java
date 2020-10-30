package me.googas.bot.api.data;

import me.googas.api.discord.RoleData;
import org.jetbrains.annotations.NotNull;

/** A role data for the bot */
public interface BotRole extends RoleData, BotPermissible {

  @Override
  @NotNull
  BotRole refresh();
}
