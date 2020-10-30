package me.googas.bot.api.events.data.user;

import me.googas.api.UserData;
import org.jetbrains.annotations.NotNull;

/** Called when the user data gets loaded */
public class UserLoadedDataEvent extends UserDataEvent {
  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserLoadedDataEvent(@NotNull UserData data) {
    super(data);
  }
}
