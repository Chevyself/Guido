package com.starfishst.bukkit.handlers.placeholders;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/** A place holder */
public interface Placeholder {

  /**
   * Builds the placeholder for the player
   *
   * @param player the player to get the placeholder built
   * @return the built placeholder
   */
  @NotNull
  String build(@NotNull OfflinePlayer player);

  /**
   * Get the name of the placeholder
   *
   * @return the name of the placeholder
   */
  @NotNull
  String getName();
}
