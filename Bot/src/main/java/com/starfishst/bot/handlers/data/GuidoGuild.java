package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.events.data.guild.BotGuildLoadedEvent;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;

import java.util.HashMap;
import java.util.Map;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild extends Catchable implements BotGuild {

  /** The unique id of the guild */
  private final transient long id;

  /**
   * The multipliers of the guild
   */
  private final HashMap<String, Integer> multipliers;

  /**
   * The ladders of the guild
   */
  private final HashMap<String, Integer> ladders;

  /**
   * The rank ranges of the guild
   */
  private final HashMap<Long, GuidoRankRange> ranges;

  /**
   * Create the guido guild
   * @param id the id of the guild
   * @param multipliers the multipliers of the guild
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   */
  public GuidoGuild(long id, HashMap<String, Integer> multipliers, HashMap<String, Integer> ladders, HashMap<Long, GuidoRankRange> ranges) {
    super(Time.fromString("3m"));
    this.id = id;
    this.multipliers = multipliers;
    this.ladders = ladders;
    this.ranges = ranges;
    new BotGuildLoadedEvent(this).call();
  }

  /** Create the guido guild. Deprecated because this type of constructor is required for GSON */
  @Deprecated
  public GuidoGuild() {
    this(0, new HashMap<>(), new HashMap<>(), new HashMap<>());
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
  public Map<String, Integer> getLadders() {
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
