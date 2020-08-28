package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.UserData;
import org.jetbrains.annotations.NotNull;

/** Called when the user data gets loaded */
public class UserDataLoadedEvent extends UserDataEvent {
  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserDataLoadedEvent(@NotNull UserData data) {
    super(data);
  }
}
