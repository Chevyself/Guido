package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commons.builder.ToStringBuilder;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/** An entry of an actual player */
public class TabListPlayerEntry implements TabListEntry {

  /** The player of this entry */
  @NonNull private final OfflinePlayer offlinePlayer;

  /**
   * Create the player entry
   *
   * @param offlinePlayer the player of the entry
   */
  public TabListPlayerEntry(@NonNull OfflinePlayer offlinePlayer) {
    this.offlinePlayer = offlinePlayer;
  }

  /**
   * Get the uuid of the entry
   *
   * @return the uuid of the entry
   */
  @Override
  public @NonNull UUID getUUID() {
    return this.offlinePlayer.getUniqueId();
  }

  /**
   * Get the name of the entry
   *
   * @return the name of the entry
   */
  @Override
  public @NonNull String getName() {
    return this.offlinePlayer.getName() == null ? "" : this.offlinePlayer.getName();
  }

  /**
   * Get the display name for the entry
   *
   * @param tab the tab that requires the name
   * @return the display name of the entry
   */
  @Override
  public @NonNull String getDisplayName(@NonNull CustomTab tab) {
    Player player = this.offlinePlayer.getPlayer();
    if (player != null && player.getUniqueId().equals(tab.getPlayer())) {
      String displayName = player.getDisplayName();
      if (displayName != null && !displayName.isEmpty()) {
        return displayName.replace(this.getName(), BukkitUtils.build("&l" + this.getName()));
      }
    } else if (player != null) {
      return player.getDisplayName();
    }
    return "";
  }

  /**
   * Get the latency of the entry
   *
   * @return the latency of the entry (ping)
   */
  @Override
  public int getLatency() {
    Player player = this.offlinePlayer.getPlayer();
    if (player != null) {
      return player.spigot().getPing();
    }
    return 9999;
  }

  /**
   * Get the game mode that the entry is in
   *
   * @return the game mode that the entry is in
   */
  @Override
  public @NonNull GameMode getGameMode() {
    Player player = this.offlinePlayer.getPlayer();
    if (player != null) {
      return player.getGameMode();
    }
    return GameMode.SURVIVAL;
  }

  /**
   * Get the entry as a wrapped game profile
   *
   * @return the wrapped game profile of the entry
   */
  @Override
  public @NonNull WrappedGameProfile toWrappedGameProfile() {
    Player player = this.offlinePlayer.getPlayer();
    if (player != null) {
      return WrappedGameProfile.fromPlayer(player);
    }
    return TabListEntry.super.toWrappedGameProfile();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("offlinePlayer", this.offlinePlayer).build();
  }
}
