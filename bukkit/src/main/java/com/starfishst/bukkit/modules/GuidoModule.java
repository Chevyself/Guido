package com.starfishst.bukkit.modules;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.config.GuidoModuleSettings;
import lombok.NonNull;
import me.googas.starbox.modules.Module;

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
  default GuidoModuleSettings getSettings() {
    return Guido.getConfiguration().getModuleSettings(this);
  }
}
