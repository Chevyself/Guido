package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.events.data.guild.BotGuildLoadedEvent;
import com.starfishst.bot.api.events.data.guild.BotGuildUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild extends Catchable implements BotGuild {

  /** The unique id of the guild */
  private final transient long id;

  /**
   * Create the guido guild
   *
   * @param id the id of the guild
   */
  public GuidoGuild(long id) {
    super(Time.fromString("3m"));
    this.id = id;
    new BotGuildLoadedEvent(this).call();
  }

  /** Create the guido guild. Deprecated because this type of constructor is required for GSON */
  @Deprecated
  public GuidoGuild() {
    this(0);
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    new BotGuildUnloadedEvent(this).call();
  }
}
