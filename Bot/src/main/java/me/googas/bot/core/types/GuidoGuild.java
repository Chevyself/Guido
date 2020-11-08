package me.googas.bot.core.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.api.matches.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.types.BotGuild;
import me.googas.commons.cache.thread.Catchable;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild extends Catchable implements BotGuild {

  /** The unique id of the guild */
  private final long id;

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
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   * @param channels the channels map of the guild
   * @param categories the categories map of the guild
   */
  public GuidoGuild(
      long id,
      @NotNull Set<Ladder> ladders,
      @NotNull HashMap<Long, RankRange> ranges,
      @NotNull HashMap<String, Long> channels,
      @NotNull HashMap<String, Long> categories) {
    this.id = id;
    this.ladders = ladders;
    this.ranges = ranges;
    this.channels = channels;
    this.categories = categories;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoGuild() {
    this(0, new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
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

  @Override
  public @NotNull Time getToRemove() {
    return new Time(10, Unit.MINUTES);
  }
}
