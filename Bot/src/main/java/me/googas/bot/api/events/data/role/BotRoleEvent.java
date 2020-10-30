package me.googas.bot.api.events.data.role;

import me.googas.bot.api.data.BotRole;
import me.googas.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event where the data of a role is involved */
public class BotRoleEvent implements GuidoEvent {

  /** The data involved in the event */
  @NotNull private final BotRole data;

  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public BotRoleEvent(@NotNull BotRole data) {
    this.data = data;
  }

  /**
   * Get the role data involved in the event
   *
   * @return the role data involved in the event
   */
  @NotNull
  public BotRole getData() {
    return this.data;
  }

  @Override
  public String toString() {
    return "BotRoleEvent{" + "data=" + this.data + '}';
  }
}
