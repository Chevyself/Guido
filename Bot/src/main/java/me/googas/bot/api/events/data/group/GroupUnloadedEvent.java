package me.googas.bot.api.events.data.group;

import lombok.NonNull;
import me.googas.api.permissions.Group;

/** Called when a group gets unloaded from cache */
public class GroupUnloadedEvent extends GroupEvent {
  /**
   * Create the event
   *
   * @param group the group involved
   */
  public GroupUnloadedEvent(@NonNull Group group) {
    super(group);
  }
}
