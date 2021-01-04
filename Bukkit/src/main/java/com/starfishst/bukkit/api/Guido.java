package com.starfishst.bukkit.api;

import com.starfishst.bukkit.CommandManager;
import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.dependencies.DependencyManager;
import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import com.starfishst.bukkit.listeners.HandlerRegistry;
import lombok.NonNull;
import me.googas.commons.Validate;
import org.bukkit.Bukkit;

/** Static utilities for guido */
public class Guido {

  /** The instance of the guido plugin */
  private static GuidoPlugin plugin;

  /**
   * Gives the instance validated to not be null
   *
   * @return the not null instance
   */
  @NonNull
  public static GuidoPlugin validated() {
    return Validate.notNull(Guido.plugin, "Guido might not have been initialized");
  }

  /**
   * Calls an event using the guido plugin
   *
   * @param event the event to call
   */
  public static void call(@NonNull GuidoEvent event) {
    Bukkit.getPluginManager().callEvent(event);
  }

  /**
   * Sets the plugin that this util is using
   *
   * @param plugin the plugin to set
   * @return
   */
  public static boolean setPlugin(GuidoPlugin plugin) {
    if (plugin != null && Guido.plugin != null) {
      return false;
    }
    Guido.plugin = plugin;
    return true;
  }

  /**
   * Get whether PAPI is connected with the plugin
   *
   * @return true if pgm is connected
   */
  public static boolean isPAPIConnected() {
    return Guido.validated().getDependencies().getDependency("PlaceholderAPI").isEnabled();
  }

  /**
   * Get whether pgm is connected with the plugin
   *
   * @return true if pgm is connected
   */
  public static boolean isPPGMConnected() {
    return Guido.validated().getDependencies().getDependency("PGM").isEnabled();
  }

  /**
   * Get whether protocol lib is connected with the plugin
   *
   * @return true if protocol lib is connected
   */
  public static boolean isProtocolLibConnected() {
    return Guido.validated().getDependencies().getDependency("ProtocolLib").isEnabled();
  }

  @NonNull
  public static BukkitLanguageHandler getBukkitLanguageHandler() {
    return Guido.validated().getBukkitLanguageHandler();
  }

  @NonNull
  public static CommandManager getManager() {
    return Guido.validated().getManager();
  }

  public static @NonNull HandlerRegistry getHandlerRegistry() {
    return Guido.validated().getHandlerRegistry();
  }

  public static @NonNull DependencyManager getDependencies() {
    return Guido.validated().getDependencies();
  }

  public static @NonNull BukkitClient getClient() {
    return Guido.validated().getClient();
  }

  @NonNull
  public static Configuration getConfiguration() {
    return Guido.validated().getConfiguration();
  }

  @NonNull
  public static BukkitLanguageHandler getLanguageHandler() {
    return Guido.validated().getLanguageHandler();
  }

  @NonNull
  public static CommandManager getCommandManager() {
    return Guido.validated().getCommandManager();
  }

  public static boolean isBungee() {
    return Bukkit.spigot().getSpigotConfig().getBoolean("setting.bungeecord", false);
  }
}
