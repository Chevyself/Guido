package com.starfishst.bot.api.events.data.user;

import com.starfishst.guido.api.data.UserData;
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
