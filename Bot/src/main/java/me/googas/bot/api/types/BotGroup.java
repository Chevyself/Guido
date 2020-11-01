package me.googas.bot.api.types;

import me.googas.api.permissions.Group;
import org.jetbrains.annotations.NotNull;

/** An extension of group for the bot */
public interface BotGroup extends Group, BotPermissible {

  @Override
  @NotNull
  BotGroup refresh();
}
