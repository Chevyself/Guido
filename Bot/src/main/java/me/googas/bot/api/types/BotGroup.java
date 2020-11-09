package me.googas.bot.api.types;

import me.googas.api.permissions.Group;

/** An extension of group for the bot */
public interface BotGroup extends Group, BotPermissible, BotCatchable {}
