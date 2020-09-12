package com.starfishst.guido.pgm.api.config;

import com.starfishst.guido.pgm.api.events.GuidoListener;
import com.starfishst.guido.pgm.api.events.GuidoListenerSettings;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/** The configuration for the guido implementation of pgm */
public interface Configuration {

  // todo
  @NotNull
  default GuidoListenerSettings getListenerSettings(@NotNull GuidoListener listener) {
    for (GuidoListenerSettings listenerSettings : this.getListenersSettings()) {
      if (listenerSettings.getName().equalsIgnoreCase(listener.getName())) {
        return listenerSettings;
      }
    }
    throw new IllegalArgumentException(listener + " is not applicable for settings");
  }

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
