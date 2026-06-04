package me.googas.api.events.group;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.permissions.Group;

/** An event with a group involved */
public class GroupEvent implements GuidoEvent {

  @NonNull @Getter private final Group group;

  /**
   * Create the event
   *
   * @param group the group involved
   */
  public GroupEvent(@NonNull Group group) {
    this.group = group;
  }

  @Override
  public String toString() {
    return "GroupEvent{" + "group=" + group + '}';
  }
}
