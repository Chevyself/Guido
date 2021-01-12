package me.googas.api.permissions;

import java.util.Collection;
import java.util.HashSet;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.client.data.permissions.SimplePermission;
import me.googas.api.client.data.permissions.SimplePermissionStack;

/** This is an entity which may posses node permissions */
public interface Permissible {

  /**
   * Checks whether the entity posses the permission and it is enabled
   *
   * @param node the node of the permission to get
   * @param context the context to get the stack
   * @return true if the entity posses the permission and it is enabled
   */
  default boolean hasPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return (stack != null && (stack.hasPermission(node))
        || (!context.equalsIgnoreCase("global") && this.hasPermission(node, "global"))
        || (!node.equalsIgnoreCase("*") && this.hasPermission("*", context)));
  }

  /**
   * Checks whether the entity contains the permission
   *
   * @param node the node of the permission to check
   * @param context the context to get the stack
   * @return true if the entity posses the permission
   */
  default boolean containsPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return (stack != null && stack.containsPermission(node))
        || (!context.equalsIgnoreCase("global") && this.containsPermission(node, "global")
            || (!node.equalsIgnoreCase("*") && this.containsPermission("*", context)));
  }

  /**
   * Get a permission stack from this permissible entity using its context
   *
   * @param context the context that differentiates the stack
   * @return the stack of permissions if found else null
   */
  @Nullable
  default PermissionStack getPermissions(@NonNull String context) {
    for (PermissionStack permission : this.getPermissions()) {
      if (permission.getContext().equalsIgnoreCase(context)) {
        return permission;
      }
    }
    return null;
  }

  /**
   * Adds a permission for this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @param expires when does the permission expire
   * @return whether the permission was added
   */
  default boolean addPermission(
      @NonNull String context, @NonNull String node, boolean enabled, long expires) {
    PermissionStack stack = this.getPermissions(context);
    if (stack == null) {
      stack = new SimplePermissionStack(context, new HashSet<>());
      this.getPermissions().add(stack);
    }
    if (!stack.containsPermission(node)) {
      SimplePermission permission = new SimplePermission(node, enabled, expires);
      return stack.add(permission);
    }
    return false;
  }

  /**
   * Removes the permission from this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @return whether the permission was removed. True if it was removed false otherwise
   */
  default boolean removePermission(@NonNull String context, @NonNull String node) {
    PermissionStack stack = this.getPermissions(context);
    if (stack != null) {
      if (stack.containsPermission(node)) {
        stack.getPermissions().removeIf(permission -> permission.getNode().equalsIgnoreCase(node));
        if (stack.getPermissions().isEmpty()) this.getPermissions().remove(stack);
        return true;
      }
    }
    return false;
  }

  /**
   * Get the set of permission stack. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @NonNull
  Collection<PermissionStack> getPermissions();
}
