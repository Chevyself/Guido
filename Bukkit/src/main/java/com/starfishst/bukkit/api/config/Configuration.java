package com.starfishst.bukkit.api.config;

import com.starfishst.bukkit.api.events.GuidoListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** The configuration for the guido implementation of pgm */
public interface Configuration {

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
      public @NotNull Map<String, Object> getMap() {
        return new HashMap<>();
      }
    };
  }

  /**
   * Get the context that the server is on
   *
   * @return the context
   */
  @NotNull
  String getContext();

  /**
   * Get the token to connect with the bot
   *
   * @return the token to authenticate
   */
  @NotNull
  String getToken();

  /**
   * Get the name of the commands that are enabled
   *
   * @return the enabled commands
   */
  @NotNull
  List<String> getEnabledCommands();

  /**
   * Get the settings for listeners
   *
   * @return the settings for listeners
   */
  @NotNull
  List<GuidoListenerSettings> getListenersSettings();
}
