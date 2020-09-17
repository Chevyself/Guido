package com.starfishst.guido.bungee.api;

import com.starfishst.core.utils.Validate;
import com.starfishst.guido.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.guido.bungee.core.GuidoPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Static utilities for the guido bungee plugin */
public class Guido {

  /** The instance of the guido plugin */
  @Nullable private static GuidoPlugin plugin;

  /**
   * Gives the instance validated to not be null
   *
   * @return the not null instance
   */
  @NotNull
  public static GuidoPlugin validated() {
    return Validate.notNull(plugin, "Guido might not have been initialized");
  }

  /**
   * Sets the plugin that this util is using
   *
   * @param plugin the plugin to set
   */
  public static void setPlugin(@Nullable GuidoPlugin plugin) {
    if (plugin != null && Guido.plugin != null) {
      throw new IllegalStateException("Plugin is already initialized");
    }
    Guido.plugin = plugin;
  }

  /**
   * Get the configuration that the guido plugin is using
   *
   * @return the configuration that the plugin is using
   */
  @NotNull
  public static BungeeConfiguration getConfiguration() {
    return validated().getBungeeConfiguration();
  }
}
