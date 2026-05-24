package com.starfishst.bukkit.modules;

import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.configuration.ModuleSettings;
import lombok.NonNull;

public interface GuidoModule extends Module {

  /**
   * Get whether this listener is enabled
   *
   * @return true if the listener is enabled
   */
  default boolean isEnabled() {
    return this.getSettings().getBoolean("enabled", false);
  }

  /**
   * Get the settings of this listener
   *
   * @return the settings of this listener
   */
  @NonNull
  default ModuleSettings getSettings() {
    return Guido.getConfiguration().getModulesSettings(this);
  }
}
