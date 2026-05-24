package me.googas.api.events.user;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.user.UserData;

/** An event that involves user data */
public class UserDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NonNull @Getter private final UserData data;

  /**
   * Create the event
   *
   * @param data the user data involved in the event
   */
  public UserDataEvent(@NonNull UserData data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "UserDataEvent{" + "data=" + data + '}';
  }
}
