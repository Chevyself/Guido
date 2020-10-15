package com.starfishst.bungee.api.configuration;

import com.starfishst.bungee.api.events.GuidoListener;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** The configuration for the guido implementation of pgm */
public interface BungeeConfiguration {

  /**
   * Get the settings for a listener
   *
   * @param listener the listener that requires the settings
   * @return the settings of the listener
   */
  @NotNull
  default GuidoListenerSettings getListenerSettings(@NotNull GuidoListener listener) {
    for (GuidoListenerSettings listenerSettings : this.getListenersSettings()) {
      if (listenerSettings.getName().equalsIgnoreCase(listener.getName())) {
        return listenerSettings;
      }
    }
    return new GuidoListenerSettings() {
      @Override
      public @NotNull String getName() {
        return listener.getName();
      }

      @Override
      public @NotNull HashMap<String, Object> getSettings() {
        return new HashMap<>();
      }
    };
  }

  /**
   * Get the token used in the bot
   *
   * @return the token used for the bot
   */
  String getToken();

  /**
   * Get the servers that can be connected in bungee
   *
   * @return the servers that can be connected in bungee
   */
  @NotNull
  List<GuidoServer> getServers();

  /**
   * Get the settings for listeners
   *
   * @return the settings for listeners
   */
  @NotNull
  List<GuidoListenerSettings> getListenersSettings();
}
