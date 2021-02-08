package com.starfishst.bukkit.api;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.dependencies.GuidoCompatibilities;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import com.starfishst.commands.bukkit.CommandManager;
import lombok.NonNull;
import me.googas.commons.Validate;
import me.googas.starbox.modules.ModuleRegistry;
import org.bukkit.Bukkit;

/** Static utilities for guido */
public class Guido {

  /** The instance of the guido plugin */
  private static GuidoPlugin plugin;

  /**
   * Gives the instance getPlugin to not be null
   *
   * @return the not null instance
   */
  @NonNull
  public static GuidoPlugin getPlugin() {
    return Validate.notNull(Guido.plugin, "Guido might not have been initialized");
  }

  public static @NonNull GuidoCompatibilities getCompatibilities() {
    return Guido.getPlugin().getCompatibilities();
  }

  public static @NonNull ModuleRegistry getModuleRegistry() {
    return Guido.getPlugin().getModuleRegistry();
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
   * Get whether pgm is connected with the plugin
   *
   * @return true if pgm is connected
   */
  public static boolean isPPGMConnected() {
    return Guido.getPlugin().getCompatibilities().getCompatibility("PGM").isEnabled();
  }

  /**
   * Get whether protocol lib is connected with the plugin
   *
   * @return true if protocol lib is connected
   */
  public static boolean isProtocolLibConnected() {
    return Guido.getPlugin().getCompatibilities().getCompatibility("ProtocolLib").isEnabled();
  }

  @NonNull
  public static BukkitLanguageHandler getBukkitLanguageHandler() {
    return Guido.getPlugin().getBukkitLanguageHandler();
  }

  @NonNull
  public static CommandManager getManager() {
    return Guido.getPlugin().getManager();
  }

  public static @NonNull BukkitClient getClient() {
    return Guido.getPlugin().getClient();
  }

  @NonNull
  public static Configuration getConfiguration() {
    return Guido.getPlugin().getConfiguration();
  }

  @NonNull
  public static BukkitLanguageHandler getLanguageHandler() {
    return Guido.getPlugin().getLanguageHandler();
  }

  @NonNull
  public static CommandManager getCommandManager() {
    return Guido.getPlugin().getCommandManager();
  }

  public static boolean isBungee() {
    return Bukkit.spigot().getSpigotConfig().getBoolean("setting.bungeecord", false);
  }
}
