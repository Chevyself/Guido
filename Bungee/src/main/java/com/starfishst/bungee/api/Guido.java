package com.starfishst.bungee.api;

import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.core.GuidoPlugin;
import com.starfishst.bungee.core.lang.BungeeLanguageHandler;
import com.starfishst.guido.api.data.implementations.ClientImpl;
import java.util.logging.Logger;
import me.googas.commons.Validate;
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
    return Validate.notNull(Guido.plugin, "Guido might not have been initialized");
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
    return Guido.validated().getBungeeConfiguration();
  }

  /**
   * Get the client used to connect with the bot
   *
   * @return the client used in the bot
   */
  @NotNull
  public static ClientImpl getClient() {
    return Guido.validated().getClient();
  }

  /**
   * Get the language handler that the plugin is using
   *
   * @return the language handler
   */
  @NotNull
  public static BungeeLanguageHandler getLanguageHandler() {
    return Guido.validated().getLanguageHandler();
  }

  /**
   * Get the logger of guido
   *
   * @return the logger of the plugin
   */
  @NotNull
  public static Logger getLogger() {
    return Guido.validated().getLogger();
  }
}
