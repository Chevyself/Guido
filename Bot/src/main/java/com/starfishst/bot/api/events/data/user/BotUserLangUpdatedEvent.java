package com.starfishst.bot.api.events.data.user;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.GuidoCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when the user data updates the language */
public class BotUserLangUpdatedEvent extends BotUserEvent implements GuidoCancellable {

  /** The new lang for the user data */
  @NotNull private final String lang;
  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   * @param lang the new lang that was set to the user
   */
  public BotUserLangUpdatedEvent(@NotNull BotUser data, @NotNull String lang) {
    super(data);
    this.lang = lang;
  }

  /**
   * Get the lang that is being set
   *
   * @return the lang that is being set
   */
  @NotNull
  public String getLang() {
    return lang;
  }

  @Override
  public void setCancelled(boolean b) {
    this.cancelled = b;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }
}
