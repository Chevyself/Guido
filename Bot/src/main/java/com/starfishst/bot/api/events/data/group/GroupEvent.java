package com.starfishst.bot.api.events.data.group;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.guido.api.data.Group;
import org.jetbrains.annotations.NotNull;

/** An event with a group involved */
public class GroupEvent implements GuidoEvent {

  /** The group involved in the event */
  @NotNull private final Group<?, ?> group;

  /**
   * Create the event
   *
   * @param group the group involved
   */
  public GroupEvent(@NotNull Group<?, ?> group) {
    this.group = group;
  }

  /**
   * Get the group involved in the event
   *
   * @return the group involved
   */
  @NotNull
  public Group<?, ?> getGroup() {
    return this.group;
  }
}
