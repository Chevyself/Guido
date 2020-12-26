package me.googas.bot.api.events.data.group;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.commons.builder.ToStringBuilder;

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
    return new ToStringBuilder(this).append("group", this.group).build();
  }
}
