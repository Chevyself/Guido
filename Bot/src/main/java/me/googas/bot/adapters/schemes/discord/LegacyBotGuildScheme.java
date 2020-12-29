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
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.ranks.GuidoRankRange;

public class LegacyBotGuildScheme implements Scheme<BotGuild> {

  private final long id;
  @NonNull private final Set<Ladder> ladders;
  // In this version rank ranges didn't have details that's why
  // the id of the role was stored outside and as the key of the map
  @NonNull private final Map<Long, RankRange> ranges;
  @NonNull private final Map<String, Long> channels;
  @NonNull private final Map<String, Long> voiceChannels;
  @NonNull private final Map<String, Long> categories;
  @NonNull private final Set<ResponsiveMesage> messages;

  public LegacyBotGuildScheme(
      long id,
      @NonNull Set<Ladder> ladders,
      @NonNull Map<Long, RankRange> ranges,
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

  /** @deprecated this constructor may only be used by gson */
  public LegacyBotGuildScheme() {
    this(
        -1,
        new HashSet<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashSet<>());
  }

  @NonNull
  public Set<RankRange> convert() {
    Set<RankRange> ranges = new HashSet<>();
    this.ranges.forEach(
        (id, range) ->
            ranges.add(
                new GuidoRankRange(
                    range.getLadder(),
                    range.getMin(),
                    range.getMax(),
                    new GuidoValuesMap("id", id))));
    return ranges;
  }

  @Override
  public @NonNull GuidoGuild build() {
    return new GuidoGuild(
        this.id,
        this.ladders,
        this.convert(),
        this.channels,
        this.voiceChannels,
        this.categories,
        this.messages);
  }
}
