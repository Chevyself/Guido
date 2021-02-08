package com.starfishst.bukkit.configuration;

import com.starfishst.bukkit.api.config.AbstractModuleSettings;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.config.GuidoModuleSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

/** The configuration for the pgm implementation */
public class GuidoConfiguration implements Configuration {

  /** The context in which the server is on */
  @NonNull private final String context;

  /** The token to connect with the bot */
  @NonNull private final String token;

  /** The set of enabled commands */
  @NonNull private final List<String> enabledCommands;

  /** The listener settings of the bot */
  @NonNull private final List<GuidoModuleSettings> settings = new ArrayList<>();

  /** Create the configuration */
  public GuidoConfiguration() {
    this.context = "Bukkit";
    this.token = "none";
    this.enabledCommands = new ArrayList<>();
  }

  /**
   * Create the configuration using a configuration section
   *
   * @param section the configuration section to use and getId the configuration
   */
  public GuidoConfiguration(@NonNull ConfigurationSection section) {
    this.context = section.getString("context", "Bukkit");
    this.token = section.getString("token", "none");
    if (section.get("enabled-commands") != null) {
      this.enabledCommands = section.getStringList("enabled-commands");
    } else {
      this.enabledCommands = new ArrayList<>();
    }
    if (section.get("listener-settings") != null) {
      ConfigurationSection settingsSection = section.getConfigurationSection("listener-settings");
      for (String name : settingsSection.getKeys(false)) {
        HashMap<String, Object> settings = new HashMap<>();
        ConfigurationSection nameSection = settingsSection.getConfigurationSection(name);
        for (String key : nameSection.getKeys(false)) {
          settings.put(key, nameSection.get(key));
        }
        this.settings.add(new AbstractModuleSettings(name, settings, nameSection));
      }
    }
  }

  @Override
  public @NonNull String getContext() {
    return this.context;
  }

  @Override
  public @NonNull String getToken() {
    return this.token;
  }

  /**
   * Get the list of enabled commands
   *
   * @return the list of enabled commands
   */
  @Override
  @NonNull
  public List<String> getEnabledCommands() {
    return this.enabledCommands;
  }

  @NonNull
  @Override
  public List<GuidoModuleSettings> getModuleSettings() {
    return this.settings;
  }
}
