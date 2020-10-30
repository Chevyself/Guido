package me.googas.bot.api.events.data.permissible;

import me.googas.api.Permission;
import me.googas.api.PermissionStack;
import me.googas.bot.api.data.BotPermissible;
import org.jetbrains.annotations.NotNull;

/** Called when a permissible gets a new permission */
public class PermissiblePermissionAddedEvent extends PermissibleEvent {

  /** The stack to which the permission was added */
  @NotNull private final PermissionStack stack;

  /** The permission that was added to the permissible */
  @NotNull private final Permission permission;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param stack the stack to which the permission was added
   * @param permission the permission that was added to the permissible
   */
  public PermissiblePermissionAddedEvent(
      @NotNull BotPermissible permissible,
      @NotNull PermissionStack stack,
      @NotNull Permission permission) {
    super(permissible);
    this.stack = stack;
    this.permission = permission;
  }

  @Override
  public String toString() {
    return "PermissiblePermissionAddedEvent{"
        + "stack="
        + this.stack
        + ", permission="
        + this.permission
        + "} "
        + super.toString();
  }
}
