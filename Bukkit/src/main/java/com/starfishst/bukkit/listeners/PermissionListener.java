package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.guido.api.data.implementations.data.PermissionImpl;
import com.starfishst.guido.api.data.implementations.data.PermissionStackImpl;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

/** Listens to changes to the player and player data to add or remove permissions */
public class PermissionListener implements GuidoListener {

  /** Permissions are given to the server in async then are added to the player */
  @NotNull private final Map<UUID, Collection<PermissionImpl>> toGive = new HashMap<>();

  /** The permissions attachment for each player */
  @NotNull private final HashMap<UUID, PermissionAttachment> attachments = new HashMap<>();

  /** The plugin is required to register the permissions and get the data for the user */
  @NotNull private final GuidoPlugin plugin;

  /**
   * Create the permissions listener
   *
   * @param plugin the plugin required for certain tasks
   */
  public PermissionListener(@NotNull GuidoPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * When a player quits the server delete the attachment
   *
   * @param event the event of removing the attachment
   */
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    player.removeAttachment(this.getAttachment(player));
    this.attachments.remove(player.getUniqueId());
  }

  /**
   * Enables a permission for a player
   *
   * @param node the node of the permission
   * @param player the player to enable the permission
   */
  public void enablePermission(@NotNull String node, @NotNull Player player) {
    this.getAttachment(player).setPermission(node, true);
  }

  /**
   * Disables a permission for a player
   *
   * @param node the node of the permission
   * @param player the player to remove the permission
   * @param force set this to true when a permission is not being removed when it is set in false.
   *     Some permissions cannot be removed, just disabled.
   */
  public void disablePermission(@NotNull String node, @NotNull Player player, boolean force) {
    this.getAttachment(player).unsetPermission(node);
    if (player.hasPermission(node) && force) {
      this.getAttachment(player).setPermission(node, false);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
    Guido.getClient()
        .request(
            new Request<>(
                PermissionStackImpl.class,
                "permission",
                Maps.objects("type", LinkedDataType.MINECRAFT)
                    .append(
                        "identification",
                        Maps.singleton("uuid", UUIDUtils.trim(event.getUniqueId())))
                    .append("context", Guido.getConfiguration().getContext())
                    .build()),
            stack -> {
              if (stack != null && !stack.getPermissions().isEmpty()) {
                this.toGive.put(event.getUniqueId(), stack.getPermissions());
              }
            });
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Collection<PermissionImpl> permissions = this.toGive.get(event.getPlayer().getUniqueId());
    if (permissions != null) {
      for (PermissionImpl permission : permissions) {
        if (permission.isEnabled()) {
          this.enablePermission(permission.getNode(), event.getPlayer());
        } else {
          this.disablePermission(permission.getNode(), event.getPlayer(), true);
        }
      }
      this.toGive.remove(event.getPlayer().getUniqueId());
    }
  }

  /**
   * Get the permission attachment for a player. If it does not have one it will be created
   *
   * @param player the player that needs the attachment
   * @return the permission attachment for the player
   */
  @NotNull
  public PermissionAttachment getAttachment(@NotNull Player player) {
    PermissionAttachment attachment = this.attachments.get(player.getUniqueId());
    if (attachment == null) {
      attachment = player.addAttachment(this.plugin);
      this.attachments.put(player.getUniqueId(), attachment);
    }
    return attachment;
  }

  @Override
  public @NotNull String getName() {
    return "permission";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void onUnload() {
    this.attachments.clear();
  }
}
