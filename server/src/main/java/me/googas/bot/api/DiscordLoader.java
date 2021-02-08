package me.googas.bot.api;

import lombok.NonNull;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.discord.GuidoRole;
import me.googas.bot.core.handlers.GuidoHandler;

public interface DiscordLoader extends GuidoHandler {

  @NonNull
  GuidoGuild getGuild(long guildId);

  @NonNull
  GuidoRole getRole(long roleId);
}
