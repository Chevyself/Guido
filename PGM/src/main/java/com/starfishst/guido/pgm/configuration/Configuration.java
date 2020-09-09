package com.starfishst.guido.pgm.configuration;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/** The configuration for the pgm implementation */
public class Configuration {

  /** The set of enabled commands */
  @NotNull private final List<String> enabledCommands;

  /** Create the configuration */
  public Configuration() {
    this.enabledCommands = new ArrayList<>();
  }

  /**
   * Create the configuration using a configuration section
   *
   * @param section the configuration section to use and get the configuration
   */
  public Configuration(@NotNull ConfigurationSection section) {
    if (section.get("enabled-commands") != null) {
      enabledCommands = section.getStringList("enabled-commands");
    } else {
      enabledCommands = new ArrayList<>();
    }
  }

  /**
   * Get the list of enabled commands
   *
   * @return the list of enabled commands
   */
  @NotNull
  public List<String> getEnabledCommands() {
    return enabledCommands;
  }
}
