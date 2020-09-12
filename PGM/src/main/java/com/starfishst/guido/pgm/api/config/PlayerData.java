package com.starfishst.guido.pgm.api.config;

import com.starfishst.guido.pgm.api.events.config.PlayerDataPermissionAddedEvent;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** The data of a player */
public interface PlayerData {

  /**
   * Adds a permission to this player. This must call {@link PlayerDataPermissionAddedEvent}
   *
   * @param permission the permission to add to the player
   */
  void addPermission(@NotNull PermissionData permission);

  /**
   * Removes a permission from this player. This must Call {@link
   * com.starfishst.guido.pgm.api.events.config.PlayerDataPermissionRemoved}
   *
   * @param node the node of the permission to remove
   */
  void removePermission(@NotNull String node);

  /**
   * Get the unique id of the player
   *
   * @return the unique id
   */
  @NotNull
  UUID getUniqueId();

  /**
   * Get the permissions that the player posses
   *
   * @return the permissions
   */
  @NotNull
  List<PermissionData> getPermissions();
}
