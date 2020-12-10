package com.starfishst.bungee.api;

import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.GuidoPlugin;
import com.starfishst.bungee.core.lang.BungeeLanguageHandler;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.client.Client;
import me.googas.commons.Validate;

/** Static utilities for the guido bungee plugin */
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
   * Delegated from {@link GuidoPlugin#getListener(Class)}
   *
   * @param clazz the class to match
   * @param <T> the type of listener to get
   * @return the listener
   */
  public static <T extends GuidoListener> T getListener(@NonNull Class<T> clazz) {
    return Guido.validated().getListener(clazz);
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
   * Get the configuration that the guido plugin is using
   *
   * @return the configuration that the plugin is using
   */
  @NonNull
  public static BungeeConfiguration getConfiguration() {
    return Guido.validated().getBungeeConfiguration();
  }

  /**
   * Get the client used to connect with the bot
   *
   * @return the client used in the bot
   */
  @NonNull
  public static Client getClient() {
    return Guido.validated().getClient();
  }

  /**
   * Get the language handler that the plugin is using
   *
   * @return the language handler
   */
  @NonNull
  public static BungeeLanguageHandler getLanguageHandler() {
    return Guido.validated().getLanguageHandler();
  }

  /**
   * Get the logger of guido
   *
   * @return the logger of the plugin
   */
  @NonNull
  public static Logger getLogger() {
    return Guido.validated().getLogger();
  }
}
