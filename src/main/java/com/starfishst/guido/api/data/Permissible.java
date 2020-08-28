package com.starfishst.guido.api.data;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** This is an entity which may posses node permissions */
public interface Permissible {

  /**
   * Checks whether the entity posses the permission and it is enabled
   *
   * @param node the node of the permission to get
   * @return true if the entity posses the permission and it is enabled
   */
  default boolean hasPermission(@NotNull String node) {
    for (Permission permission : this.getPermissions()) {
      if (permission.getNode().equalsIgnoreCase(node) && permission.isEnabled()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the set of permissions. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @NotNull
  Set<Permission> getPermissions();
}
