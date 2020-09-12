package com.starfishst.guido.pgm.configuration;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.pgm.api.config.PermissionData;
import com.starfishst.guido.pgm.api.config.PlayerData;
import com.starfishst.guido.pgm.api.events.config.PlayerDataPermissionAddedEvent;
import com.starfishst.guido.pgm.api.events.config.PlayerDataPermissionRemoved;
import com.starfishst.guido.pgm.api.events.config.PlayerDataUnloadedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** The data of a player */
public class GuidoPlayer extends Catchable implements PlayerData {

  /** The unique id of the player */
  @NotNull private final UUID uniqueId;

  /** The permissions of the player */
  @NotNull private final List<GuidoPermission> permissions;

  /**
   * Create the guido player
   *
   * @param uniqueId the unique id of the player
   * @param permissions the permissions of the player
   */
  public GuidoPlayer(@NotNull UUID uniqueId, @NotNull List<GuidoPermission> permissions) {
    super(Time.fromString("5m"));
    this.uniqueId = uniqueId;
    this.permissions = permissions;
  }

  /** Create the guido player. This constructor is used for GSON */
  @Deprecated
  public GuidoPlayer() {
    this(UUID.randomUUID(), new ArrayList<>());
  }

  /**
   * Check whether this player contains certain permission
   *
   * @param node the node of the permission to check
   * @return true if this contains the permission
   */
  private boolean containsPermission(@NotNull String node) {
    for (GuidoPermission guidoPermission : this.permissions) {
      if (guidoPermission.getNode().equalsIgnoreCase(node)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check whether this player contains certain permission
   *
   * @param permission the permission to check
   * @return true if this contains the permission
   */
  private boolean containsPermission(@NotNull PermissionData permission) {
    return this.containsPermission(permission.getNode());
  }

  @Override
  public void onSecondsPassed() {
    Player player = Bukkit.getPlayer(this.getUniqueId());
    if (player != null && player.isOnline()) {
      this.refresh();
    }
  }

  @Override
  public void onRemove() {
    new PlayerDataUnloadedEvent(this).call();
  }

  @Override
  public @NotNull UUID getUniqueId() {
    return this.uniqueId;
  }

  @Override
  public @NotNull List<PermissionData> getPermissions() {
    return new ArrayList<>(this.permissions);
  }

  @Override
  public void addPermission(@NotNull PermissionData permission) {
    if (!this.containsPermission(permission)) {
      if (!(permission instanceof GuidoPermission)) {
        permission = new GuidoPermission(permission.getNode(), permission.isEnabled());
      }
      this.permissions.add((GuidoPermission) permission);
      new PlayerDataPermissionAddedEvent(this, permission).call();
    }
  }

  @Override
  public void removePermission(@NotNull String node) {
    if (this.containsPermission(node)) {
      this.permissions.removeIf(
          guidoPermission -> guidoPermission.getNode().equalsIgnoreCase(node));
      new PlayerDataPermissionRemoved(this, node).call();
    }
  }
}
