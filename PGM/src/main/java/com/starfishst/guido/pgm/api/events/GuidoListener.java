package com.starfishst.guido.pgm.api.events;

import com.starfishst.guido.pgm.api.Guido;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** A listener for the guido plugin */
public interface GuidoListener extends Listener {

  /**
   * Register the listener for a plugin
   *
   * @param plugin the plugin to register the listener to
   */
  default void register(@NotNull Plugin plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  /** Unregisters the listener */
  default void unregister() {
    this.onUnload();
    HandlerList.unregisterAll(this);
  }

  /** Called on {@link #unregister()} */
  void onUnload();

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @NotNull
  String getName();

  /**
   * Get whether this listener is enabled
   *
   * @return true if the listener is enabled
   */
  boolean isEnabled();

  /**
   * Get the settings of this listener
   *
   * @return the settings of this listener
   */
  @NotNull
  default GuidoListenerSettings getSettings() {
    return Guido.getConfiguration().getListenerSettings(this);
  }
}
