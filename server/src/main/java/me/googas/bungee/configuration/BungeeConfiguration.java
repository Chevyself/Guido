package me.googas.bungee.configuration;

import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import me.googas.bungee.events.GuidoListener;

/** The configuration for the guido implementation of pgm */
public interface BungeeConfiguration {

  /**
   * Get the settings for a listener
   *
   * @param listener the listener that requires the settings
   * @return the settings of the listener
   */
  @NonNull
  default GuidoListenerSettings getListenerSettings(@NonNull GuidoListener listener) {
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
      public @NonNull HashMap<String, Object> getSettings() {
        return new HashMap<>();
      }
    };
  }

  @NonNull
  String getBotArguments();

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
  @NonNull
  List<GuidoServer> getServers();

  /**
   * Get the settings for listeners
   *
   * @return the settings for listeners
   */
  @NonNull
  List<GuidoListenerSettings> getListenersSettings();
}
