package me.googas.api.permissions;

import java.util.Collection;
import lombok.NonNull;
import me.googas.annotations.Nullable;

/**
 * A permission stack contains permissions with certain context to use its permissions in different
 * situations
 */
public interface PermissionStack {

  /**
   * Check whether this permission stack has a permission
   *
   * @param node the node of the permission
   * @return true if this has the permission and is enabled
   */
  default boolean hasPermission(@NonNull String node) {
    for (Permission permission : this.getPermissions()) {
      if (permission.getNode().equalsIgnoreCase(node)
          && permission.isEnabled()
          && !permission.isExpired()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether this stack has a permission and if it is disabled
   *
   * @param node the node of the permission
   * @return true if the stack has the permission
   */
  default boolean containsPermission(@NonNull String node) {
    for (Permission permission : this.getPermissions()) {
      if (permission.getNode().equalsIgnoreCase(node)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a permission to the set
   *
   * @param perm the permission to add
   * @return whether the permission was added
   */
  default boolean add(@NonNull Permission perm) {
    return this.getPermissions().add(perm);
  }

  /**
   * Removes a permission to the set
   *
   * @param perm the permission to remove
   * @return whether the permission was removed
   */
  default boolean remove(@NonNull Permission perm) {
    return this.getPermissions().remove(perm);
  }

  default boolean addAll(@Nullable PermissionStack global) {
    if (global == null) return false;
    return this.getPermissions().addAll(global.getPermissions());
  }

  /**
   * Get the context of the permission stack
   *
   * @return the context of the permission stack
   */
  @NonNull
  String getContext();

  /**
   * Get the permissions that the stack has
   *
   * @return the permission that the stack has
   */
  @NonNull
  Collection<Permission> getPermissions();
}
