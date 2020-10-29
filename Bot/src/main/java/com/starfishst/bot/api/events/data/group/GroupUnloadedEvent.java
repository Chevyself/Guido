package com.starfishst.bot.api.events.data.group;

import me.googas.api.Group;
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
