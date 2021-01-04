package com.starfishst.bukkit.api.events;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.config.GuidoListenerSettings;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/** A listener for the guido plugin */
public interface Handler extends Listener {

  /**
   * Register the listener for a plugin
   *
   * @param plugin the plugin to register the listener to
   * @return this same handler instance
   */
  default Handler register(@NonNull Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    return this;
  }

  /** Unregisters the listener */
  default void unregister() {
    HandlerList.unregisterAll(this);
  }

  /** Called before {@link #unregister()} */
  default void onDisable() {}

  /** Called after {@link #register(Plugin)} */
  default void onEnable() {}

  /**
   * Whether this handler has receptors to be registered in the client
   *
   * @return by default is false and must be overridden by the handler
   */
  default boolean hasReceptors() {
    return false;
  }

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @NonNull
  String getName();

  /**
   * Get whether this listener is enabled
   *
   * @return true if the listener is enabled
   */
  default boolean isEnabled() {
    return this.getSettings().getOr("enabled", Boolean.class, false);
  }

  /**
   * Get the settings of this listener
   *
   * @return the settings of this listener
   */
  @NonNull
  default GuidoListenerSettings getSettings() {
    return Guido.getConfiguration().getListenerSettings(this);
  }
}
