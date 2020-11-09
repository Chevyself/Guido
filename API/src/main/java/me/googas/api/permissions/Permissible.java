package me.googas.api.permissions;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This is an entity which may posses node permissions */
public interface Permissible {

  /**
   * Checks whether the entity posses the permission and it is enabled
   *
   * @param node the node of the permission to get
   * @param context the context to get the stack
   * @return true if the entity posses the permission and it is enabled
   */
  default boolean hasPermission(@NotNull String node, @NotNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return (stack != null && (stack.hasPermission(node) || stack.hasPermission("*"))
        || (!context.equalsIgnoreCase("global") && this.hasPermission(node, "global"))
        || (this.hasPermission("*", context)));
  }

  /**
   * Checks whether the entity contains the permission
   *
   * @param node the node of the permission to check
   * @param context the context to get the stack
   * @return true if the entity posses the permission
   */
  default boolean containsPermission(@NotNull String node, @NotNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return (stack != null && stack.containsPermission(node))
        || (!context.equalsIgnoreCase("global") && this.containsPermission(node, "global")
            || (this.hasPermission("*", context)));
  }

  /**
   * Get a permission stack from this permissible entity using its context
   *
   * @param context the context that differentiates the stack
   * @return the stack of permissions if found else null
   */
  @Nullable
  default PermissionStack getPermissions(@NotNull String context) {
    for (PermissionStack permission : this.getPermissions()) {
      if (permission.getContext().equalsIgnoreCase(context)) {
        return permission;
      }
    }
    return null;
  }

  /**
   * Get the set of permission stack. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @NotNull
  Collection<PermissionStack> getPermissions();

  /**
   * Adds a permission for this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @return whether the permission was added
   */
  boolean addPermission(@NotNull String context, @NotNull String node, boolean enabled);

  /**
   * Removes the permission from this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @return whether the permission was removed. True if it was removed false otherwise
   */
  boolean removePermission(@NotNull String context, @NotNull String node);
}
