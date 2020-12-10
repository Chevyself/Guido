package me.googas.bot.api.events.data.group;

import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.bot.api.events.GuidoEvent;

/** An event with a group involved */
public class GroupEvent implements GuidoEvent {

  /** The group involved in the event */
  @NonNull private final Group group;

  /**
   * Create the event
   *
   * @param group the group involved
   */
  public GroupEvent(@NonNull Group group) {
    this.group = group;
  }

  /**
   * Get the group involved in the event
   *
   * @return the group involved
   */
  @NonNull
  public Group getGroup() {
    return this.group;
  }
}
