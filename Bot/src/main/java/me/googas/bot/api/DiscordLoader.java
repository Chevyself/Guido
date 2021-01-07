package me.googas.bot.api;

import lombok.NonNull;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.core.handlers.GuidoHandler;

public interface DiscordLoader extends GuidoHandler {

  @NonNull
  BotGuild getGuild(long guildId);

  @NonNull
  BotRole getRole(long roleId);
}
