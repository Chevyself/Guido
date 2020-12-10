package me.googas.bot.api.events.data.user;

import lombok.NonNull;
import me.googas.api.user.UserData;

/** Called when the user data gets unloaded */
public class UserUnloadedDataEvent extends UserDataEvent {
  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserUnloadedDataEvent(@NonNull UserData data) {
    super(data);
  }
}
