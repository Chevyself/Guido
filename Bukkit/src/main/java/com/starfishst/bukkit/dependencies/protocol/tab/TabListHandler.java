package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.starfishst.bukkit.dependencies.protocol.PacketHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commons.Lots;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/** Controls the tab list for players */
public class TabListHandler extends PacketAdapter implements PacketHandler {

  /** The players that are joining the game */
  @NonNull private final Set<UUID> joining = new HashSet<>();

  /** The custom tabs loaded */
  @NonNull private final Set<CustomTab> tabs = new HashSet<>();

  /**
   * Create the handler
   *
   * @param plugin the plugin using the handler
   */
  public TabListHandler(@NonNull Plugin plugin) {
    super(plugin, PacketType.Play.Server.PLAYER_INFO);
  }

  /**
   * Get the packet info action that is being applied
   *
   * @param packet the packet to get the info action
   * @return the info action of the packet
   */
  public static EnumWrappers.PlayerInfoAction getAction(@NonNull PacketContainer packet) {
    return packet.getPlayerInfoAction().read(0);
  }

  /**
   * When a player joins the server add them to the joined players
   *
   * @param event the event of a player joining
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
    this.joining.add(event.getUniqueId());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerJoinEvent(PlayerJoinEvent event) {
    this.joining.removeIf(uuid -> uuid.equals(event.getPlayer().getUniqueId()));
  }

  /**
   * Get the custom tab of a gamer
   *
   * @param offlinePlayer the player to get the custom tab
   * @return the custom tab
   */
  @NonNull
  public CustomTab getCustomTab(@NonNull OfflinePlayer offlinePlayer) {
    for (CustomTab tab : this.tabs) {
      if (tab.getPlayer().equals(offlinePlayer.getUniqueId())) {
        return tab;
      }
    }
    CustomTab customTab = new CustomTab(offlinePlayer.getUniqueId(), Lots.list());
    for (int i = 0; i < 10; i++) {
      customTab.getSlots().add(new TabSlotPlayer(0, new TabListPlayerEmptyEntry()));
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      TabSlotPlayer slot = customTab.getEmptySlot(player);
      if (!player.getUniqueId().equals(offlinePlayer.getUniqueId()) && slot != null) {
        slot.setEntry(new TabListPlayerEntry(player));
      }
    }
    this.tabs.add(customTab);
    return customTab;
  }

  public boolean isJoining(@NonNull PlayerInfoData playerData) {
    UUID uuid = playerData.getProfile().getUUID();
    for (UUID joiningUuid : this.joining) {
      if (joiningUuid.equals(uuid)) return true;
    }
    return false;
  }

  private boolean isJoining(@NonNull OfflinePlayer player) {
    for (UUID uuid : this.joining) {
      if (uuid.equals(player.getUniqueId())) return true;
    }
    return false;
  }

  @Override
  public @NonNull String getName() {
    return "tab-list";
  }

  @Override
  public void onPacketSending(PacketEvent event) {
    Player player = event.getPlayer();
    EnumWrappers.PlayerInfoAction action = TabListHandler.getAction(event.getPacket());
    CustomTab tab = this.getCustomTab(player);
    switch (action) {
      case ADD_PLAYER:
        if (this.isJoining(event.getPlayer())) {
          event
              .getPacket()
              .getPlayerInfoDataLists()
              .write(
                  0,
                  tab.toPlayerInfoData(event.getPacket().getPlayerInfoDataLists().read(0), true));
        } else {
          event
              .getPacket()
              .getPlayerInfoDataLists()
              .write(
                  0,
                  tab.toPlayerInfoData(event.getPacket().getPlayerInfoDataLists().read(0), false));
        }
        tab.sendTeamPacket(0);
        break;
      case REMOVE_PLAYER:
        tab.removePlayersFromData(event.getPacket().getPlayerInfoDataLists().read(0));
        break;
      case UPDATE_GAME_MODE:
      case UPDATE_LATENCY:
      case UPDATE_DISPLAY_NAME:
        event
            .getPacket()
            .getPlayerInfoDataLists()
            .write(
                0, tab.toPlayerInfoData(event.getPacket().getPlayerInfoDataLists().read(0), false));
        break;
    }
  }

  @Override
  public void onDisable() {
    for (CustomTab tab : this.tabs) {
      tab.exit();
    }
    this.tabs.clear();
  }
}
