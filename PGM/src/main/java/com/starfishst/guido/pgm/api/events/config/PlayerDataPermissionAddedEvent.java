package com.starfishst.guido.pgm.api.events.config;

import com.starfishst.guido.pgm.api.config.PermissionData;
import com.starfishst.guido.pgm.api.config.PlayerData;
import org.jetbrains.annotations.NotNull;

/** Called when a permission is added to a player data */
public class PlayerDataPermissionAddedEvent extends PlayerDataEvent {

  /** The permission that was added to the player */
  @NotNull private final PermissionData permission;

  /**
   * Create the event
   *
   * @param data the player data involved in the event
   * @param permission the permission that was added to the player
   */
  public PlayerDataPermissionAddedEvent(
      @NotNull PlayerData data, @NotNull PermissionData permission) {
    super(data);
    this.permission = permission;
  }

  /**
   * Get the permission that was added to the player
   *
   * @return the permission that was added
   */
  @NotNull
  public PermissionData getPermission() {
    return permission;
  }
}
