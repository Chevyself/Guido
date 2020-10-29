package com.starfishst.bot.handlers.data.types;

import com.starfishst.bot.api.events.data.user.UserLoadedDataEvent;
import com.starfishst.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.api.UserData;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements UserData {

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
    new UserLoadedDataEvent(this).call();
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoUser() {
    this("-1");
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new UserUnloadedDataEvent(this).call();
  }

  @Override
  @NotNull
  public String getId() {
    return this.id;
  }

  @Override
  public @NotNull GuidoUser refresh() {
    return (GuidoUser) super.refresh();
  }
}
