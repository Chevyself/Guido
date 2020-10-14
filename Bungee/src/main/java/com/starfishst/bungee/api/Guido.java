package com.starfishst.bungee.api;

import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.core.GuidoPlugin;
import com.starfishst.guido.api.data.implementations.ImplementationClient;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.googas.commons.Validate;
import me.googas.commons.cache.ICatchable;
import me.googas.messaging.Request;
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

  /**
   * Send a request or get the object from cache
   *
   * @param request the request to make in case the object is not in cache
   * @param predicate the boolean to get the object from cache
   * @param consumer the method to do after we get the object
   * @param <T> the type of the object
   */
  public static <T extends ICatchable> void request(
      @NotNull Request<T> request,
      @NotNull Predicate<ICatchable> predicate,
      @NotNull Consumer<T> consumer) {
    // TODO
  }

  /**
   * Get the client used to connect with the bot
   *
   * @return the client used in the bot
   */
  @NotNull
  public static ImplementationClient getClient() {
    return validated().getClient();
  }
}
