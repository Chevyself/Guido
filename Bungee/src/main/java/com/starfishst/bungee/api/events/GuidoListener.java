package com.starfishst.bungee.api.events;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.configuration.GuidoListenerSettings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** Listens for bungee events */
public interface GuidoListener extends Listener {

  /**
   * Registers the listener for a plugin
   *
   * @param plugin the plugin to register the listener to
   */
  default void register(@NotNull Plugin plugin) {
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }

  /** Unregisters the listener */
  default void unregister() {
    this.onUnload();
    ProxyServer.getInstance().getPluginManager().unregisterListener(this);
  }

  /** Called on {@link #unregister()} */
  void onUnload();

  /**
   * Get the name of the listener
   *
   * @return the name of the listener
   */
  @NotNull
  String getName();

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
