package me.googas.bot.api.events.data.role;

import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.BotRole;

/** An event where the data of a role is involved */
public class BotRoleEvent implements GuidoEvent {

  /** The data involved in the event */
  @NonNull private final BotRole data;

  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleEvent(@NonNull BotRole data) {
    this.data = data;
  }

  /**
   * Get the role data involved in the event
   *
   * @return the role data involved in the event
   */
  @NonNull
  public BotRole getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "BotRoleEvent{" + "data=" + this.data + '}';
  }
}
