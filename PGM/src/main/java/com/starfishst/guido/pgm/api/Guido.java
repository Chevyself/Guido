package com.starfishst.guido.pgm.api;

import com.starfishst.guido.pgm.GuidoPlugin;
import com.starfishst.guido.pgm.api.config.Configuration;
import com.starfishst.guido.pgm.api.config.DataLoader;
import com.starfishst.guido.pgm.api.config.PlayerData;
import com.starfishst.guido.pgm.api.events.GuidoEvent;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import java.util.UUID;
import me.googas.commons.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Static utilities for guido */
public class Guido {

  /** The instance of the guido plugin */
  @Nullable private static GuidoPlugin plugin;

  /**
   * Get a listener using its class
   *
   * @param clazz the class of the listener
   * @param <T> the type of the listener class
   * @return the listener if found null if it might not have been registered
   */
  public static <T extends GuidoListener> @Nullable T getListener(@NotNull Class<T> clazz) {
    return validated().getListener(clazz);
  }

  /**
   * Get whether pgm is connected with the plugin
   *
   * @return true if pgm is connected
   */
  public static boolean isPgmConnected() {
    return validated().getDependencies().getDependency("PGM").isEnabled();
  }

  /**
   * Calls an event using the guido plugin
   *
   * @param event the event to call
   */
  public static void call(@NotNull GuidoEvent event) {
    Bukkit.getPluginManager().callEvent(event);
  }

  /**
   * Get the data of a player using its unique id
   *
   * @param uuid the unique id of the player
   * @return the data of the player if found else create a new one
   */
  @NotNull
  public static PlayerData getData(@NotNull UUID uuid) {
    return validated().getLoader().getPlayerData(uuid);
  }

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
   * Get the data loader that the plugin is using
   *
   * @return the data loader
   */
  @NotNull
  public static DataLoader getLoader() {
    return validated().getLoader();
  }

  /**
   * Get the configuration that the plugin is using
   *
   * @return the plugin configuration
   */
  @NotNull
  public static Configuration getConfiguration() {
    return validated().getConfiguration();
  }

  /**
   * Get whether protocol lib is connected with the plugin
   *
   * @return true if protocol lib is connected
   */
  public static boolean isProtocolLibConnected() {
    return validated().getDependencies().getDependency("ProtocolLib").isEnabled();
  }
}
