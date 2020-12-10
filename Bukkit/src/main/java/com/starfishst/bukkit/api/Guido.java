package com.starfishst.bukkit.api;

import com.starfishst.bukkit.CommandManager;
import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.Validate;
import org.bukkit.Bukkit;

/** Static utilities for guido */
public class Guido {

  /** The instance of the guido plugin */
  private static GuidoPlugin plugin;

  /**
   * Get a listener using its class
   *
   * @param clazz the class of the listener
   * @param <T> the type of the listener class
   * @return the listener if found null if it might not have been registered
   */
  public static <T extends GuidoListener> T getListener(@NonNull Class<T> clazz) {
    return Guido.validated().getListener(clazz);
  }

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
   * Get whether pgm is connected with the plugin
   *
   * @return true if pgm is connected
   */
  public static boolean isPgmConnected() {
    return Guido.validated().getDependencies().getDependency("PGM").isEnabled();
  }

  /**
   * Sets the plugin that this util is using
   *
   * @param plugin the plugin to set
   */
  public static void setPlugin(GuidoPlugin plugin) {
    if (plugin != null && Guido.plugin != null) {
      throw new IllegalStateException("Plugin is already initialized");
    }
    Guido.plugin = plugin;
  }

  /**
   * Get the configuration that the plugin is using
   *
   * @return the plugin configuration
   */
  @NonNull
  public static Configuration getConfiguration() {
    return Guido.validated().getConfiguration();
  }

  /**
   * Get whether protocol lib is connected with the plugin
   *
   * @return true if protocol lib is connected
   */
  public static boolean isProtocolLibConnected() {
    return Guido.validated().getDependencies().getDependency("ProtocolLib").isEnabled();
  }

  /**
   * Get the client of the implementation
   *
   * @return the client
   */
  @NonNull
  public static Client getClient() {
    return Guido.validated().getClient();
  }

  /**
   * Get the language handler of the plugin
   *
   * @return the language handler
   */
  @NonNull
  public static BukkitLanguageHandler getLanguageHandler() {
    return Guido.validated().getLanguageHandler();
  }

  /**
   * Get the logger of the plugin
   *
   * @return the logger of the plugin
   */
  @NonNull
  public static Logger getLogger() {
    return Guido.validated().getLogger();
  }

  /**
   * Get the command manager that the bot is using
   *
   * @return the command manager
   */
  @NonNull
  public static CommandManager getCommandManager() {
    return Guido.validated().getCommandManager();
  }
}
