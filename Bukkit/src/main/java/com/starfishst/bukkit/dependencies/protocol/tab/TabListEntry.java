package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.GameMode;

/** An entry inside the tab list */
public interface TabListEntry {

  /**
   * Get the display name for the entry
   *
   * @param tab the tab that requires the name
   * @return the display name of the entry
   */
  @NonNull
  String getDisplayName(@NonNull CustomTab tab);

  /**
   * Get the display name of the entry as a wrapped chat component
   *
   * @param tab the custom tab that requires the name
   * @return the chat component of the display name
   */
  @NonNull
  default WrappedChatComponent getWrappedDisplayName(@NonNull CustomTab tab) {
    return WrappedChatComponent.fromText(this.getDisplayName(tab));
  }

  /**
   * Get the entry as a wrapped game profile
   *
   * @return the wrapped game profile of the entry
   */
  @NonNull
  default WrappedGameProfile toWrappedGameProfile() {
    return new WrappedGameProfile(this.getUUID(), this.getName());
  }

  /**
   * Get the entry as player info data
   *
   * @param tab the tab that request the info data
   * @return the player info data
   */
  @NonNull
  default PlayerInfoData toPlayerInfoData(@NonNull CustomTab tab) {
    return new PlayerInfoData(
        this.toWrappedGameProfile(),
        this.getLatency(),
        this.getNativeGameMode(),
        this.getWrappedDisplayName(tab));
  }

  /**
   * Get the uuid of the entry
   *
   * @return the uuid of the entry
   */
  @NonNull
  UUID getUUID();

  /**
   * Get the name of the entry
   *
   * @return the name of the entry
   */
  @NonNull
  String getName();

  /**
   * Get the latency of the entry
   *
   * @return the latency of the entry (ping)
   */
  int getLatency();

  /**
   * Get the game mode that the entry is in
   *
   * @return the game mode that the entry is in
   */
  @NonNull
  GameMode getGameMode();

  /**
   * Get the game mode of the entry in native
   *
   * @return the native game mode of the entry
   */
  @NonNull
  default EnumWrappers.NativeGameMode getNativeGameMode() {
    return EnumWrappers.NativeGameMode.fromBukkit(this.getGameMode());
  }
}
