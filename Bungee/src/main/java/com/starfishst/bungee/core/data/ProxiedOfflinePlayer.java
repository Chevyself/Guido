package com.starfishst.bungee.core.data;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
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

  public ProxiedOfflinePlayer(@NonNull MinecraftLinkable ref) {
    this(ref.getUuid(), ref.getNickname());
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

  /**
   * Get the link information of this offline proxied player
   *
   * @return the link information
   */
  @NonNull
  public LinkableInfo getLinkedInfo() {
    return new SimpleLinkableInfo(
        LinkableType.MINECRAFT,
        new SimpleValuesMap(Maps.objects("uuid", UUIDUtils.trim(this.getUniqueId())).build()));
  }
}
