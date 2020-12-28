package me.googas.bot.adapters.schemes.discord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.discord.GuidoGuild;

public class LatestBotGuildScheme implements Scheme<BotGuild> {

  private final long id;
  @NonNull private final Set<Ladder> ladders;
  @NonNull private final Set<RankRange> ranges;
  @NonNull private final Map<String, Long> channels;
  @NonNull private final Map<String, Long> voiceChannels;
  @NonNull private final Map<String, Long> categories;
  @NonNull private final Set<ResponsiveMesage> messages;

  public LatestBotGuildScheme(
      long id,
      @NonNull Set<Ladder> ladders,
      @NonNull Set<RankRange> ranges,
      @NonNull Map<String, Long> channels,
      @NonNull Map<String, Long> voiceChannels,
      @NonNull Map<String, Long> categories,
      @NonNull Set<ResponsiveMesage> messages) {
    this.id = id;
    this.ladders = ladders;
    this.ranges = ranges;
    this.channels = channels;
    this.voiceChannels = voiceChannels;
    this.categories = categories;
    this.messages = messages;
  }

  public LatestBotGuildScheme() {
    this(
        -1,
        new HashSet<>(),
        new HashSet<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashSet<>());
  }

  @Override
  public @NonNull GuidoGuild build() {
    return new GuidoGuild(
        this.id,
        this.ladders,
        this.ranges,
        this.channels,
        this.voiceChannels,
        this.categories,
        this.messages);
  }
}
