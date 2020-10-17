package com.starfishst.bungee.core.data;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** An offline proxied player */
public class ProxiedOfflinePlayer {

  /** The uuid of the offline player */
  @NotNull private final UUID uuid;

  /** The nick of the offline user */
  @NotNull private final String nickname;

  /**
   * Create the player
   *
   * @param uuid the uuid of the player
   * @param nickname the nickname of the offline user
   */
  public ProxiedOfflinePlayer(@NotNull UUID uuid, @NotNull String nickname) {
    this.uuid = uuid;
    this.nickname = nickname;
  }

  /**
   * Get the unique id of the player
   *
   * @return the unique id
   */
  @NotNull
  public UUID getUniqueId() {
    return this.uuid;
  }

  /**
   * Get the nickname of the offline user
   *
   * @return the nickname
   */
  @NotNull
  public String getNickname() {
    return this.nickname;
  }
}
