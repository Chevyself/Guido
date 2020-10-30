package me.googas.bot.api.data;

import me.googas.api.Group;
import org.jetbrains.annotations.NotNull;

/** An extension of group for the bot */
public interface BotGroup extends Group, BotPermissible {

  @Override
  @NotNull
  BotGroup refresh();
}
