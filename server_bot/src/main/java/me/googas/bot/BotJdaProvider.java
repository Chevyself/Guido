package me.googas.bot;

import lombok.NonNull;
import me.googas.api.links.JdaProvider;
import me.googas.bot.core.discord.GuidoGuild;

public interface BotJdaProvider extends JdaProvider {
  @NonNull
  GuidoGuild getGuidoGuild();
}
