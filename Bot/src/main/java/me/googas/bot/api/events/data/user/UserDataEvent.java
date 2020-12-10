package me.googas.bot.api.events.data.user;

import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.api.events.GuidoEvent;

/** An event that involves user data */
public class UserDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NonNull private final UserData data;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserDataEvent(@NonNull UserData data) {
    this.data = data;
  }

  /**
   * Get the user data involved in the event
   *
   * @return the user data involved in the event
   */
  @NonNull
  public UserData getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "UserDataEvent{" + "data=" + this.data + '}';
  }
}
