package com.starfishst.bungee.core.data;

import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.HashMap;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
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
   * Get the unique id of the player.
   *
   * @deprecated use {@link #getParams()} when allowed
   * @return the unique id
   */
  @NotNull
  public UUID getUniqueId() {
    return this.uuid;
  }

  /**
   * Get the nickname of the offline user
   *
   * @deprecated use {@link #getParams()} when allowed
   * @return the nickname
   */
  @NotNull
  public String getNickname() {
    return this.nickname;
  }

  /**
   * Get the map of parameters for the player
   *
   * @return the parameters of the player
   */
  public HashMap<String, Object> getParams() {
    return this.getBuilder().build();
  }

  /**
   * Get the map of parameters builder for the player
   *
   * @return the parameters of the player
   */
  public MapBuilder<String, Object> getBuilder() {
    return Maps.objects(
            "identification", Maps.singleton("uuid", UUIDUtils.trim(this.getUniqueId())))
        .append("type", LinkedDataType.MINECRAFT);
  }
}
