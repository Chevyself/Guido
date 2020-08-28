package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.events.GuidoCancellable;
import org.jetbrains.annotations.NotNull;

/** Called when the user data updates the language */
public class UserDataLangUpdatedEvent extends UserDataEvent implements GuidoCancellable {

  /** The new lang for the user data */
  @NotNull private final String lang;
  /** Whether the event is cancelled */
  private boolean cancelled;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   * @param lang
   */
  public UserDataLangUpdatedEvent(@NotNull UserData data, String lang) {
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
