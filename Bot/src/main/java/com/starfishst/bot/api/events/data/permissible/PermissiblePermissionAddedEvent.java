package com.starfishst.bot.api.events.data.permissible;

import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.PermissionStack;
import org.jetbrains.annotations.NotNull;

/** Called when a permissible gets a new permission */
public class PermissiblePermissionAddedEvent extends PermissibleEvent {

  /** The stack to which the permission was added */
  @NotNull private final PermissionStack<?> stack;

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
      @NotNull Permissible<?> permissible,
      @NotNull PermissionStack<?> stack,
      @NotNull Permission permission) {
    super(permissible);
    this.stack = stack;
    this.permission = permission;
  }
}
