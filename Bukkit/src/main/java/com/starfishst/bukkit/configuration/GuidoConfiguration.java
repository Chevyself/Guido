package com.starfishst.bukkit.configuration;

import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.config.GuidoListenerSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/** The configuration for the pgm implementation */
public class GuidoConfiguration implements Configuration {

  /** The set of enabled commands */
  @NotNull private final List<String> enabledCommands;

  /** The listener settings of the bot */
  @NotNull private final List<GuidoListenerSettings> settings = new ArrayList<>();

  /** Create the configuration */
  public GuidoConfiguration() {
    this.enabledCommands = new ArrayList<>();
  }

  /**
   * Create the configuration using a configuration section
   *
   * @param section the configuration section to use and get the configuration
   */
  public GuidoConfiguration(@NotNull ConfigurationSection section) {
    if (section.get("enabled-commands") != null) {
      this.enabledCommands = section.getStringList("enabled-commands");
    } else {
      this.enabledCommands = new ArrayList<>();
    }
    if (section.get("listener-settings") != null) {
      ConfigurationSection settingsSection = section.getConfigurationSection("listener-settings");
      for (String name : settingsSection.getKeys(false)) {
        // TODO a proper class for this
        HashMap<String, Object> settings = new HashMap<>();
        ConfigurationSection nameSection = settingsSection.getConfigurationSection(name);
        for (String key : nameSection.getKeys(false)) {
          settings.put(key, nameSection.get(key));
        }
        this.settings.add(
            new GuidoListenerSettings() {
              @Override
              public @NotNull String getName() {
                return name;
              }

              @Override
              public @NotNull HashMap<String, Object> getSettings() {
                return settings;
              }
            });
      }
    }
  }

  /**
   * Get the list of enabled commands
   *
   * @return the list of enabled commands
   */
  @Override
  @NotNull
  public List<String> getEnabledCommands() {
    return this.enabledCommands;
  }

  @NotNull
  @Override
  public List<GuidoListenerSettings> getListenersSettings() {
    return this.settings;
  }
}
