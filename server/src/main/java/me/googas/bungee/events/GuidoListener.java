package me.googas.bungee.events;

import lombok.NonNull;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.configuration.GuidoListenerSettings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/** Listens for bungee events */
public interface GuidoListener extends Listener {

  /**
   * Registers the listener for a plugin
   *
   * @param plugin the plugin to register the listener to
   */
  default void register(@NonNull Plugin plugin) {
    plugin.getProxy().getPluginManager().registerListener(plugin, this);
  }

  /** Unregisters the listener */
  default void unregister() {
    this.onUnload();
    ProxyServer.getInstance().getPluginManager().unregisterListener(this);
  }

  /** Called on {@link #unregister()} */
  default void onUnload() {}

  /** Called after {@link #onEnable()} */
  default void onEnable() {}

  /**
   * Get the name of the listener
   *
   * @return the name of the listener
   */
  @NonNull
  String getName();

  /**
   * Get the settings of this listener
   *
   * @return the settings of this listener
   */
  @NonNull
  default GuidoListenerSettings getSettings() {
    return GuidoBungee.getConfiguration().getListenerSettings(this);
  }
}
