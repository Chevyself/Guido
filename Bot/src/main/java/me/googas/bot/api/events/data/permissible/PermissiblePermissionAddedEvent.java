package me.googas.bot.api.events.data.permissible;

import lombok.NonNull;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.types.BotPermissible;

/** Called when a permissible gets a new permission */
public class PermissiblePermissionAddedEvent extends PermissibleEvent {

  /** The stack to which the permission was added */
  @NonNull private final PermissionStack stack;

  /** The permission that was added to the permissible */
  @NonNull private final Permission permission;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param stack the stack to which the permission was added
   * @param permission the permission that was added to the permissible
   */
  public PermissiblePermissionAddedEvent(
      @NonNull BotPermissible permissible,
      @NonNull PermissionStack stack,
      @NonNull Permission permission) {
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
