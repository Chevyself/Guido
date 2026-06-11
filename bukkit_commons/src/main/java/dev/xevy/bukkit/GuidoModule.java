package dev.xevy.bukkit;

import lombok.NonNull;
import me.googas.starbox.modules.Module;

public interface GuidoModule extends Module {

  @NonNull
  GuidoBukkitRuntime getRuntime();

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
    return this.getRuntime().getConfiguration().getModulesSettings(this);
  }
}
