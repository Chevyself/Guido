package me.googas.api.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

/**
 * A permission stack contains permissions with certain context to use its permissions in different
 * situations
 */
public class PermissionStack {

  @NonNull @Getter private final String context;
  @NonNull @Getter private final List<AbstractPermission> permissions;

  /**
   * Create the stack
   *
   * @param context the context of the stack
   * @param permissions the permissions of the stack
   */
  public PermissionStack(@NonNull String context, @NonNull List<AbstractPermission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public PermissionStack() {
    this("", new ArrayList<>());
  }

  /**
   * Check whether this permission stack has a permission
   *
   * @param node the node of the permission
   * @return true if this has the permission and is enabled
   */
  public boolean hasPermission(@NonNull String node) {
    for (AbstractPermission abstractPermission : this.getPermissions()) {
      if (abstractPermission.getNode().equalsIgnoreCase(node)
          && abstractPermission.isEnabled()
          && !abstractPermission.isExpired()) {
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
  public boolean containsPermission(@NonNull String node) {
    for (AbstractPermission abstractPermission : this.getPermissions()) {
      if (abstractPermission.getNode().equalsIgnoreCase(node)) {
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
  public boolean add(@NonNull AbstractPermission perm) {
    return this.getPermissions().add(perm);
  }

  /**
   * Removes a permission to the set
   *
   * @param perm the permission to remove
   * @return whether the permission was removed
   */
  public boolean remove(@NonNull AbstractPermission perm) {
    return this.getPermissions().remove(perm);
  }

  public boolean addAll(PermissionStack global) {
    if (global == null) return false;
    return this.getPermissions().addAll(global.getPermissions());
  }

  /**
   * Get the stack as a map with the nodes of the permissions and whether those are enabled. This
   * will not contain the expired permissions nor the context of the stack
   *
   * @return the map
   */
  @NonNull
  public Map<String, Boolean> toMap() {
    Map<String, Boolean> map = new HashMap<>();
    for (AbstractPermission permission : this.getPermissions()) {
      if (!permission.isExpired()) {
        map.put(permission.getNode(), permission.isEnabled());
      }
    }
    return map;
  }
}
