package com.starfishst.bukkit.dependencies.protocol.tab;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.handlers.placeholders.PlaceholderHandler;
import lombok.NonNull;

/** A tab list entry that can be replaced */
public class TabListPlaceholderEntry extends TabListEmptyEntry {

  /** The protocol manager to send the update */
  @NonNull
  private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

  /** The raw string to replace */
  @NonNull private final String raw;

  /**
   * Create the tab list holder
   *
   * @param raw the raw string to replace
   */
  public TabListPlaceholderEntry(@NonNull String raw) {
    this.raw = raw;
  }

  public PlaceholderHandler placeholders() {
    return Guido.getHandlerRegistry().getHandler(PlaceholderHandler.class);
  }

  /**
   * Get the display name of the entry
   *
   * @return the display name of the entry
   */
  @NonNull
  @Override
  public String getDisplayName(@NonNull CustomTab tab) {
    return this.placeholders().build(tab.bukkitOfflinePlayer(), this.raw);
  }
}
