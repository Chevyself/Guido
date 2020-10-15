package com.starfishst.bungee.core.data;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** An offline proxied player */
public class ProxiedOfflinePlayer {

  /** The uuid of the offline player */
  @NotNull private final UUID uuid;

  /**
   * Create the player
   *
   * @param uuid the uuid of the player
   */
  public ProxiedOfflinePlayer(@NotNull UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Get the unique id of the player
   *
   * @return the unique id
   */
  @NotNull
  public UUID getUniqueId() {
    return uuid;
  }
}
