package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.client.requests.BukkitBooleanRequest;
import com.starfishst.bukkit.client.requests.BukkitRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

/** Listens to changes to the player and player data to add or remove permissions */
public class PermissionHandler implements Handler {

  /** Permissions are given to the server in async then are added to the player */
  @NonNull private final Map<UUID, Collection<Permission>> toGive = new HashMap<>();

  /** The permissions attachment for each player */
  @NonNull private final HashMap<UUID, PermissionAttachment> attachments = new HashMap<>();

  /** The plugin is required to register the permissions and get the data for the user */
  @NonNull private final GuidoPlugin plugin;

  /**
   * Create the permissions listener
   *
   * @param plugin the plugin required for certain tasks
   */
  public PermissionHandler(@NonNull GuidoPlugin plugin) {
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
    UUID uniqueId = player.getUniqueId();
    player.removeAttachment(this.getAttachment(player));
    this.attachments.remove(uniqueId);
  }

  /**
   * Enables a permission for a player
   *
   * @param node the node of the permission
   * @param player the player to enable the permission
   */
  public void enablePermission(@NonNull String node, @NonNull Player player) {
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
  public void disablePermission(@NonNull String node, @NonNull Player player, boolean force) {
    this.getAttachment(player).unsetPermission(node);
    if (player.hasPermission(node) && force) {
      this.getAttachment(player).setPermission(node, false);
    }
  }

  /**
   * Listen to when a player logs in to give them a permission
   *
   * @param event the event of a player prematurely joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
    UUID uniqueId = event.getUniqueId();
    try {
      if (!this.checkBungee(event)) return;
      LinkableInfo link =
          new SimpleLinkableInfo(
              LinkableType.MINECRAFT,
              new SimpleValuesMap(Maps.singleton("uuid", UUIDUtils.trim(uniqueId))));
      String context = Guido.getConfiguration().getContext();
      PermissionStack stack =
          new BukkitRequest<>(
                  PermissionStack.class,
                  "link/permissions",
                  Maps.objects("link", link).append("context", context).append("global", true))
              .send();
      Set<Permission> permissionsToGive = new HashSet<>();
      if (stack != null && !stack.getPermissions().isEmpty()) {
        permissionsToGive.addAll(stack.getPermissions());
      }
      this.toGive.put(uniqueId, permissionsToGive);
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check if the connecting player is connected thru bungee
   *
   * @param event the event of a player joining the game
   * @return true if the player can join the server
   * @throws MessengerListenFailException in case that the request goes wrong
   */
  public boolean checkBungee(@NonNull AsyncPlayerPreLoginEvent event)
      throws MessengerListenFailException {
    if (Guido.isBungee()) {
      Boolean bol =
          new BukkitBooleanRequest("bungee/is-online", Maps.singleton("uuid", event.getUniqueId()))
              .send();
      if (bol != null) {
        if (!bol) {
          event.disallow(
              AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
              "You must connect thru the bungee server");
          return false;
        } else {
          return true;
        }
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  /**
   * Replace or add the permission
   *
   * @param permissions the collection of permissions to replace
   * @param permission the permission to add
   */
  public void replaceOrAdd(
      @NonNull Collection<Permission> permissions, @NonNull Permission permission) {
    permissions.removeIf(perm -> perm.getNode().equalsIgnoreCase(permission.getNode()));
    permissions.add(permission);
  }

  /**
   * Listen to when the player has joined the game to give them the permissions
   *
   * @param event the event of a player joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoin(PlayerLoginEvent event) {
    Collection<Permission> permissions = this.toGive.get(event.getPlayer().getUniqueId());
    if (permissions != null) {
      for (Permission permission : permissions) {
        if (permission.isEnabled()) {
          this.enablePermission(permission.getNode(), event.getPlayer());
        } else {
          this.disablePermission(permission.getNode(), event.getPlayer(), true);
        }
      }
    }
    event.getPlayer().recalculatePermissions();
  }

  /**
   * Get the permission attachment for a player. If it does not have one it will be created
   *
   * @param player the player that needs the attachment
   * @return the permission attachment for the player
   */
  @NonNull
  public PermissionAttachment getAttachment(@NonNull Player player) {
    PermissionAttachment attachment = this.attachments.get(player.getUniqueId());
    if (attachment == null) {
      attachment = player.addAttachment(this.plugin);
      this.attachments.put(player.getUniqueId(), attachment);
    }
    return attachment;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public void onDisable() {
    this.attachments.clear();
  }

  @Override
  public @NonNull String getName() {
    return "permission";
  }
}
