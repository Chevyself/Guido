package me.googas.bot.api.events.data.permissible;

import me.googas.api.Permissible;
import me.googas.bot.api.data.BotPermissible;
import me.googas.bot.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event that involves a {@link Permissible} */
public class PermissibleEvent implements GuidoEvent {

  /** The permissible involved in the event */
  @NotNull private final BotPermissible permissible;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   */
  public PermissibleEvent(@NotNull BotPermissible permissible) {
    this.permissible = permissible;
  }

  /**
   * Get the permissible involved in the event
   *
   * @return the permissible
   */
  @NotNull
  public BotPermissible getPermissible() {
    return this.permissible;
  }

  @Override
  public String toString() {
    return "PermissibleEvent{" + "permissible=" + this.permissible + '}';
  }
}
