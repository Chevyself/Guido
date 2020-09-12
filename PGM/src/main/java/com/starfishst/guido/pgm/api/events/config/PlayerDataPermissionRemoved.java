package com.starfishst.guido.pgm.api.events.config;

import com.starfishst.guido.pgm.api.config.PlayerData;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a player gets its permission removed */
public class PlayerDataPermissionRemoved extends PlayerDataEvent {

  /** The node of the permission that was removed */
  @NotNull private final String node;

  /**
   * Create the event
   *
   * @param data the player data involved in the event
   * @param node the node of the string that was removed
   */
  public PlayerDataPermissionRemoved(@NotNull PlayerData data, @NotNull String node) {
    super(data);
    this.node = node;
  }

  /**
   * Get the node of the permission that was removed
   *
   * @return the node of the permission
   */
  @NotNull
  public String getNode() {
    return node;
  }
}
