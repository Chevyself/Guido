package me.googas.bot.handlers.data.types.permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.Permission;
import me.googas.api.PermissionStack;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission stacks */
public class GuidoPermissionStack implements PermissionStack {

  /** The context of the permission stack */
  @NotNull private final String context;

  /** The permissions inside the stack */
  @NotNull private final Set<Permission> permissions;

  /**
   * Create the permission stack
   *
   * @param context the context of the stack
   * @param permissions the permissions inside the stack
   */
  public GuidoPermissionStack(@NotNull String context, @NotNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoPermissionStack() {
    this("", new HashSet<>());
  }

  /**
   * Get the context of the permission stack
   *
   * @return the context of the permission stack
   */
  @Override
  public @NotNull String getContext() {
    return this.context;
  }

  /**
   * Get the permissions that the stack has
   *
   * @return the permission that the stack has
   */
  @Override
  public @NotNull Collection<Permission> getPermissions() {
    return this.permissions;
  }

  @Override
  public String toString() {
    return "GuidoPermissionStack{"
        + "context='"
        + this.context
        + '\''
        + ", permissions="
        + this.permissions
        + '}';
  }
}
