package me.googas.api.client.data;

import java.util.Collection;
import java.util.Set;
import me.googas.api.Permission;
import me.googas.api.PermissionStack;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission stack */
public class PermissionStackImpl implements PermissionStack {

  /** The context of the stack */
  @NotNull private final String context;

  /** The permissions of the stack */
  @NotNull private final Set<Permission> permissions;

  /**
   * Create the stack
   *
   * @param context the context of the stack
   * @param permissions the permissions of the stack
   */
  public PermissionStackImpl(@NotNull String context, @NotNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  @Override
  public @NotNull String getContext() {
    return this.context;
  }

  @Override
  public @NotNull Collection<Permission> getPermissions() {
    return this.permissions;
  }

  @Override
  public String toString() {
    return "PermissionStack{"
        + "context='"
        + this.context
        + '\''
        + ", permissions="
        + this.permissions
        + '}';
  }
}
