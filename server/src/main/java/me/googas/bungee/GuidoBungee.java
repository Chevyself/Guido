package me.googas.bungee;

import java.util.Objects;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.bungee.configuration.BungeeConfiguration;
import me.googas.bungee.events.GuidoListener;
import me.googas.bungee.lang.BungeeLanguageHandler;

/** Static utilities for the guido bungee plugin */
public class GuidoBungee {

  /** The instance of the guido plugin */
  private static GuidoPlugin plugin;

  /**
   * Gives the instance validated to not be null
   *
   * @return the not null instance
   */
  @NonNull
  public static GuidoPlugin validated() {
    return Objects.requireNonNull(
        GuidoBungee.plugin, "GuidoBungee might not have been initialized");
  }

  /**
   * Delegated from {@link GuidoPlugin#getListener(Class)}
   *
   * @param clazz the class to match
   * @param <T> the type of listener to getId
   * @return the listener
   */
  public static <T extends GuidoListener> T getListener(@NonNull Class<T> clazz) {
    return GuidoBungee.validated().getListener(clazz);
  }

  /**
   * Sets the plugin that this util is using
   *
   * @param plugin the plugin to set
   */
  public static void setPlugin(GuidoPlugin plugin) {
    if (plugin != null && GuidoBungee.plugin != null) {
      throw new IllegalStateException("Plugin is already initialized");
    }
    GuidoBungee.plugin = plugin;
  }

  public static boolean isBungee() {
    return GuidoBungee.plugin != null;
  }

  @NonNull
  public static BungeeConfiguration getConfiguration() {
    return GuidoBungee.validated().getConfiguration();
  }

  @NonNull
  public static BungeeLanguageHandler getLanguageHandler() {
    return GuidoBungee.validated().getLanguageHandler();
  }

  @NonNull
  public static Logger getLogger() {
    return GuidoBungee.validated().getLogger();
  }
}
