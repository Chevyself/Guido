package me.googas.bot.api.events.data.permissible;

import lombok.NonNull;
import me.googas.api.permissions.Permissible;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.api.types.BotPermissible;

/** An event that involves a {@link Permissible} */
public class PermissibleEvent implements GuidoEvent {

  /** The permissible involved in the event */
  @NonNull private final BotPermissible permissible;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   */
  public PermissibleEvent(@NonNull BotPermissible permissible) {
    this.permissible = permissible;
  }

  /**
   * Get the permissible involved in the event
   *
   * @return the permissible
   */
  @NonNull
  public BotPermissible getPermissible() {
    return this.permissible;
  }

  @Override
  public String toString() {
    return "PermissibleEvent{" + "permissible=" + this.permissible + '}';
  }
}
