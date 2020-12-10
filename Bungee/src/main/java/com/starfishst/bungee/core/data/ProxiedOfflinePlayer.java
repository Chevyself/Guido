package com.starfishst.bungee.core.data;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.SimpleLinkableInfo;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;

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

  /**
   * Get the unique id of the player.
   *
   * @deprecated use {@link #getParams()} when allowed
   * @return the unique id
   */
  @NonNull
  public UUID getUniqueId() {
    return this.uuid;
  }

  /**
   * Get the nickname of the offline user
   *
   * @deprecated use {@link #getParams()} when allowed
   * @return the nickname
   */
  @NonNull
  public String getNickname() {
    return this.nickname;
  }

  /**
   * Get the map of parameters for the player
   *
   * @deprecated use {@link #getLinkedInfo()}
   * @return the parameters of the player
   */
  public Map<String, Object> getParams() {
    return this.getBuilder().build();
  }

  /**
   * Get the map of parameters builder for the player
   *
   * @deprecated use {@link #getLinkedInfo()}
   * @return the parameters of the player
   */
  public MapBuilder<String, Object> getBuilder() {
    return Maps.objects(
            "identification", Maps.singleton("uuid", UUIDUtils.trim(this.getUniqueId())))
        .append("type", LinkableType.MINECRAFT);
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
        new SimpleValuesMap(
            Maps.objects("uuid", UUIDUtils.trim(this.getUniqueId()))
                .append("nickname", this.getNickname())
                .build()));
  }
}
