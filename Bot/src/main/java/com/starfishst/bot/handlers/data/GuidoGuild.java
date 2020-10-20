package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.events.data.guild.BotGuildLoadedEvent;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild extends Catchable implements BotGuild {

  /** The unique id of the guild */
  private final long id;

  /** The multipliers of the guild */
  private final HashMap<String, Integer> multipliers;

  /** The ladders of the guild */
  private final Set<GuidoLadder> ladders;

  /** The rank ranges of the guild */
  private final HashMap<Long, GuidoRankRange> ranges;

  /**
   * Create the guido guild
   *
   * @param id the id of the guild
   * @param multipliers the multipliers of the guild
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   */
  public GuidoGuild(
      long id,
      HashMap<String, Integer> multipliers,
      Set<GuidoLadder> ladders,
      HashMap<Long, GuidoRankRange> ranges) {
    super(Time.fromString("10m"));
    this.id = id;
    this.multipliers = multipliers;
    this.ladders = ladders;
    this.ranges = ranges;
    new BotGuildLoadedEvent(this).call();
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoGuild() {
    this(0, new HashMap<>(), new HashSet<>(), new HashMap<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public Map<String, Integer> getMultipliers() {
    return this.multipliers;
  }

  @Override
  public Set<GuidoLadder> getLadders() {
    return this.ladders;
  }

  @Override
  public Map<Long, GuidoRankRange> getRanges() {
    return this.ranges;
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotGuildUnloadedEvent(this).call();
  }
}
