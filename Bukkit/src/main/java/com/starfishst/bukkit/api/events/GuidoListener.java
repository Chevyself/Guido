package com.starfishst.bukkit.api.events;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.config.GuidoListenerSettings;
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/** A listener for the guido plugin */
public interface GuidoListener extends Listener {

  /**
   * Register the listener for a plugin
   *
   * @param plugin the plugin to register the listener to
   */
  default void register(@NonNull Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /** Unregisters the listener */
  default void unregister() {
    this.onUnload();
    HandlerList.unregisterAll(this);
  }

  /** Called on {@link #unregister()} */
  default void onUnload() {}

  /** Called after {@link #register(Plugin)} */
  default void onEnable() {}

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
