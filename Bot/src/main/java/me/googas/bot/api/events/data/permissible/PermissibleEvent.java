package me.googas.bot.api.events.data.permissible;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.permissions.Permissible;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.commons.builder.ToStringBuilder;

/** An event that involves a {@link Permissible} */
public class PermissibleEvent implements GuidoEvent {

  /** The permissible involved in the event */
  @NonNull @Getter private final Permissible permissible;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   */
  public PermissibleEvent(@NonNull Permissible permissible) {
    this.permissible = permissible;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("permissible", this.permissible).build();
  }
}
