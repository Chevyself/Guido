package com.starfishst.bukkit.api.config;

import com.starfishst.bukkit.api.events.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/** The configuration for the guido implementation of pgm */
public interface Configuration {

  /**
   * Get the settings for a listener
   *
   * @param listener the listener that requires the settings
   * @return the settings of the listener
   */
  @NonNull
  default GuidoListenerSettings getListenerSettings(@NonNull Handler listener) {
    for (GuidoListenerSettings listenerSettings : this.getListenersSettings()) {
      if (listenerSettings.getName().equalsIgnoreCase(listener.getName())) {
        return listenerSettings;
      }
    }
    return new GuidoListenerSettings() {
      @Override
      public @NonNull String getName() {
        return listener.getName();
      }

      @Override
      public @NonNull Map<String, Object> getMap() {
        return new HashMap<>();
      }
    };
  }

  /**
   * Get the context that the server is on
   *
   * @return the context
   */
  @NonNull
  String getContext();

  /**
   * Get the token to connect with the bot
   *
   * @return the token to authenticate
   */
  @NonNull
  String getToken();

  /**
   * Get the name of the commands that are enabled
   *
   * @return the enabled commands
   */
  @NonNull
  List<String> getEnabledCommands();

  /**
   * Get the settings for listeners
   *
   * @return the settings for listeners
   */
  @NonNull
  List<GuidoListenerSettings> getListenersSettings();
}
