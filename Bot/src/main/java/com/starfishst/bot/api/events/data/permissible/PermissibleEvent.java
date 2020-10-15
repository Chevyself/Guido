package com.starfishst.bot.api.events.data.permissible;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.guido.api.data.Permissible;
import org.jetbrains.annotations.NotNull;

/** An event that involves a {@link com.starfishst.guido.api.data.Permissible} */
public class PermissibleEvent implements GuidoEvent {

  /** The permissible involved in the event */
  @NotNull private final Permissible<?> permissible;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   */
  public PermissibleEvent(@NotNull Permissible<?> permissible) {
    this.permissible = permissible;
  }

  /**
   * Get the permissible involved in the event
   *
   * @return the permissible
   */
  @NotNull
  public Permissible<?> getPermissible() {
    return this.permissible;
  }
}
