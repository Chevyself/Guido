package me.googas.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.events.permissible.PermissiblePermissionAddedEvent;
import me.googas.api.events.permissible.PermissiblePermissionRemovedEvent;

/** This is an entity which may posses node permissions */
public interface Permissible {

  /**
   * Checks whether the entity posses the permission and it is enabled
   *
   * @param node the node of the permission to getId
   * @param context the context to getId the stack
   * @return true if the entity posses the permission and it is enabled
   */
  default boolean hasPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return stack.hasPermission(node)
        || !context.equalsIgnoreCase("global") && this.hasPermission(node, "global")
        || !node.equalsIgnoreCase("*") && this.hasPermission("*", context);
  }

  /**
   * Checks whether the entity contains the permission
   *
   * @param node the node of the permission to check
   * @param context the context to getId the stack
   * @return true if the entity posses the permission
   */
  default boolean containsPermission(@NonNull String node, @NonNull String context) {
    PermissionStack stack = this.getPermissions(context);
    return stack.containsPermission(node)
        || !context.equalsIgnoreCase("global") && this.containsPermission(node, "global")
        || !node.equalsIgnoreCase("*") && this.containsPermission("*", context);
  }

  /**
   * Get a permission stack from this permissible entity using its context
   *
   * @param context the context that differentiates the stack
   * @return the stack of permissions if found else null
   */
  @NonNull
  default PermissionStack getPermissions(@NonNull String context) {
    for (PermissionStack permission : this.getPermissions()) {
      if (permission.getContext().equalsIgnoreCase(context)) {
        return permission;
      }
    }
    return new PermissionStack(context, new ArrayList<>());
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
    if (!stack.containsPermission(node)) {
      AbstractPermission permission = new AbstractPermission(node, enabled, expires);
      new PermissiblePermissionAddedEvent(this, context, node, enabled, expires).call();
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
    if (stack.containsPermission(node)) {
      stack.getPermissions().removeIf(permission -> permission.getNode().equalsIgnoreCase(node));
      if (stack.getPermissions().isEmpty()) this.getPermissions().remove(stack);
      new PermissiblePermissionRemovedEvent(this, context, node).call();
      return true;
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

  /**
   * Get the permissions of this permissible as a map
   *
   * @see PermissionStack#toMap()
   * @return the map of the permissible
   */
  default Map<String, Map<String, Boolean>> toMap() {
    Map<String, Map<String, Boolean>> map = new HashMap<>();
    for (PermissionStack permission : this.getPermissions()) {
      map.put(permission.getContext(), permission.toMap());
    }
    return map;
  }
}
