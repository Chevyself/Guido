package com.starfishst.bot.api.events.data.user;

import com.starfishst.bot.api.events.GuidoEvent;
import me.googas.api.UserData;
import org.jetbrains.annotations.NotNull;

/** An event that involves user data */
public class UserDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NotNull private final UserData data;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserDataEvent(@NotNull UserData data) {
    this.data = data;
  }

  /**
   * Get the user data involved in the event
   *
   * @return the user data involved in the event
   */
  @NotNull
  public UserData getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "UserDataEvent{" + "data=" + this.data + '}';
  }
}
