package me.googas.bot.api.events.data.group;

import me.googas.api.permissions.Group;
import org.jetbrains.annotations.NotNull;

/** Called when a group gets unloaded from cache */
public class GroupUnloadedEvent extends GroupEvent {
  /**
   * Create the event
   *
   * @param group the group involved
   */
  public GroupUnloadedEvent(@NotNull Group group) {
    super(group);
  }
}
