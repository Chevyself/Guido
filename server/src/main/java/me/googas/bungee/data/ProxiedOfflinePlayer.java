package me.googas.bungee.data;

import java.util.UUID;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/** An offline proxied player */
public class ProxiedOfflinePlayer {

  /** The uuid of the offline player */
  @NonNull private final UUID uuid;

  /** The nick of the offline user */
  @NonNull private final String nickname;

  /**
   * Create the player
   *
   * @param uuid the uuid of the player
   * @param nickname the nickname of the offline user
   */
  public ProxiedOfflinePlayer(@NonNull UUID uuid, @NonNull String nickname) {
    this.uuid = uuid;
    this.nickname = nickname;
  }

  public ProxiedOfflinePlayer(@NonNull ProxiedPlayer player) {
    this(player.getUniqueId(), player.getName());
  }

  public ProxiedPlayer toProxy() {
    return ProxyServer.getInstance().getPlayer(this.uuid);
  }

  /**
   * Get the unique id of the player.
   *
   * @return the unique id
   */
  @NonNull
  public UUID getUniqueId() {
    return this.uuid;
  }

  /**
   * Get the nickname of the offline user
   *
   * @return the nickname
   */
  @NonNull
  public String getNickname() {
    return this.nickname;
  }
}
