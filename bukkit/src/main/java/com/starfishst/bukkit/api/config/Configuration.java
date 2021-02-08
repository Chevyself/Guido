package com.starfishst.bukkit.api.config;

import com.starfishst.bukkit.modules.GuidoModule;
import java.util.List;
import lombok.NonNull;

/** The configuration for the guido implementation of pgm */
public interface Configuration {

  /**
   * Get the settings for a module
   *
   * @param module the module that requires the settings
   * @return the settings of the module
   */
  @NonNull
  default GuidoModuleSettings getModuleSettings(@NonNull GuidoModule module) {
    for (GuidoModuleSettings moduleSettings : this.getModuleSettings()) {
      if (moduleSettings.getName().equalsIgnoreCase(module.getName())) {
        return moduleSettings;
      }
    }
    return new AbstractModuleSettings(module.getName());
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
  List<GuidoModuleSettings> getModuleSettings();
}
