package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.events.GuidoListener;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;

/** Listens to changes to the player and player data to add or remove permissions */
public class PermissionListener implements GuidoListener {

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
