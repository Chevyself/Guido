package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.events.data.guild.BotGuildLoadedEvent;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.guido.api.data.RankRange;
import com.starfishst.guido.api.data.matches.Ladder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild extends Catchable implements BotGuild {

  /** The unique id of the guild */
  private final long id;

  /** The multipliers of the guild */
  @NotNull private final Map<String, Integer> multipliers;

  /** The ladders of the guild */
  @NotNull private final Set<Ladder> ladders;

  /** The rank ranges of the guild */
  @NotNull private final Map<Long, RankRange> ranges;

  /** The map of channels and its ids for the guild */
  @NotNull private final Map<String, Long> channels;

  /** The map of categories and its ids for the guild */
  @NotNull private final Map<String, Long> categories;

  /**
   * Create the guido guild
   *
   * @param id the id of the guild
   * @param multipliers the multipliers of the guild
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   * @param channels the channels map of the guild
   * @param categories the categories map of the guild
   */
  public GuidoGuild(
      long id,
      @NotNull HashMap<String, Integer> multipliers,
      @NotNull Set<Ladder> ladders,
      @NotNull HashMap<Long, RankRange> ranges,
      @NotNull HashMap<String, Long> channels,
      @NotNull HashMap<String, Long> categories) {
    super(Time.fromString("10m"));
    this.id = id;
    this.multipliers = multipliers;
    this.ladders = ladders;
    this.ranges = ranges;
    this.channels = channels;
    this.categories = categories;
    new BotGuildLoadedEvent(this).call();
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoGuild() {
    this(0, new HashMap<>(), new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @NotNull
  @Override
  public Map<String, Integer> getMultipliers() {
    return this.multipliers;
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

  @Override
  public @NotNull Map<String, Long> getCategories() {
    return this.categories;
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotGuildUnloadedEvent(this).call();
  }
}
