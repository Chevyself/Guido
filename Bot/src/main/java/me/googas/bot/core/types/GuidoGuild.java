package me.googas.bot.core.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.api.matches.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.types.BotGuild;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild implements BotGuild {

  /** The unique id of the guild */
  private final long id;

  /** The ladders of the guild */
  @NotNull private final Set<Ladder> ladders;

  /** The rank ranges of the guild */
  @NotNull private final Map<Long, RankRange> ranges;

  /** The map of channels and its ids for the guild */
  @NotNull private final Map<String, Long> channels;

  /** The map of voice channels and its ids for the guild */
  @NotNull private final Map<String, Long> voiceChannels;

  /** The map of categories and its ids for the guild */
  @NotNull private final Map<String, Long> categories;

  /**
   * Create the guido guild
   *
   * @param id the id of the guild
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   * @param channels the channels map of the guild
   * @param voiceChannels the voice channels map of the guild
   * @param categories the categories map of the guild
   */
  public GuidoGuild(
      long id,
      @NotNull Set<Ladder> ladders,
      @NotNull Map<Long, RankRange> ranges,
      @NotNull Map<String, Long> channels,
      @NotNull Map<String, Long> voiceChannels,
      @NotNull HashMap<String, Long> categories) {
    this.id = id;
    this.ladders = ladders;
    this.ranges = ranges;
    this.channels = channels;
    this.voiceChannels = voiceChannels;
    this.categories = categories;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoGuild() {
    this(0, new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @NotNull
  @Override
  public Set<Ladder> getLadders() {
    return this.ladders;
  }

  @NotNull
  @Override
  public Map<Long, RankRange> getRanges() {
    return this.ranges;
  }

  @Override
  public @NotNull Map<String, Long> getChannels() {
    return this.channels;
  }

  /**
   * This map contains the string to identify a voice channel and its id
   *
   * @return the map of channels
   */
  @Override
  public @NotNull Map<String, Long> getVoiceChannels() {
    return null;
  }

  @Override
  public @NotNull Map<String, Long> getCategories() {
    return this.categories;
  }

  @Override
  public void onRemove() {
    new BotGuildUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(10, Unit.MINUTES);
  }

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NotNull GuidoGuild cache() {
    return (GuidoGuild) BotGuild.super.cache();
  }
}
