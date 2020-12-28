package me.googas.bot.adapters.discord;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.bot.adapters.SchemeDeserializer;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.adapters.schemes.discord.LatestBotGuildScheme;
import me.googas.bot.adapters.schemes.discord.LegacyBotGuildScheme;
import me.googas.bot.api.types.discord.BotGuild;

public class BotGuildDeserializer implements SchemeDeserializer<BotGuild> {

  @NonNull private final Map<String, Class<? extends Scheme<BotGuild>>> schemes = new HashMap<>();

  public BotGuildDeserializer() {
    this.schemes.put("legacy", LegacyBotGuildScheme.class);
    this.schemes.put("PRE-3", LatestBotGuildScheme.class);
  }

  @Override
  public @NonNull Map<String, Class<? extends Scheme<BotGuild>>> getSchemes() {
    return this.schemes;
  }

  @Override
  public @NonNull String getLatest() {
    return "PRE-3";
  }

  @Override
  public boolean isEmptyAsLatest() {
    return false;
  }
}
