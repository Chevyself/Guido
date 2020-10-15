package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.data.user.BotUserLoadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements BotUser {

  /** The unique id of the user */
  private final String id;

  /**
   * Create the guido user
   *
   * @param id the user id
   */
  public GuidoUser(@NotNull String id) {
    super(Time.fromString("5m"));
    this.id = id;
    new BotUserLoadedEvent(this).call();
  }

  /** Create the guido user. This is deprecated because only gson may use it */
  @Deprecated
  public GuidoUser() {
    this("-1");
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  @NotNull
  public String getId() {
    return this.id;
  }
}
