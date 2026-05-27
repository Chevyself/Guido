package com.starfishst.bukkit.configuration;

import com.starfishst.bukkit.modules.GuidoModule;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/** The configuration for the pgm implementation */
public class GuidoConfiguration {

  /** The context in which the server is on */
  @NonNull @Getter private final String context;
  /** The token to connect with the bot */
  @NonNull @Getter private final String token;

  /** The set of enabled commands */
  @NonNull @Getter private final List<String> commands;
  /** The listener settings of the bot */
  @NonNull @Getter private final List<ModuleSettings> modulesSettings;

  public GuidoConfiguration(
      @NonNull String context,
      @NonNull String token,
      @NonNull List<String> commands,
      @NonNull List<ModuleSettings> modulesSettings) {
    this.context = context;
    this.token = token;
    this.commands = commands;
    this.modulesSettings = modulesSettings;
  }

  public GuidoConfiguration() {
    this("", "", new ArrayList<>(), new ArrayList<>());
  }

  @NonNull
  public static GuidoConfiguration load(ConfigurationSection section) {
    if (section == null) return new GuidoConfiguration();
    return new GuidoConfiguration(
        section.getString("context", "Bukkit"),
        section.getString("token", "none"),
        section.getStringList("commands"),
        ModuleSettings.loadAll(section.getConfigurationSection("modules")));
  }

  @NonNull
  public static GuidoConfiguration load(@NonNull Plugin plugin)
      throws IOException, InvalidConfigurationException {
    // TODO add exceptions to fallback
    String name = "config.yml"; // This could be configurable maybe
    InputStream resource = plugin.getResource(name);
    if (resource == null)
      throw new IllegalArgumentException(plugin + " does not have the resource `" + name + "`");
    InputStreamReader reader = new InputStreamReader(resource);
    YamlConfiguration defaults = new YamlConfiguration();
    try {
      defaults.load(reader);
    } catch (InvalidConfigurationException | IOException e) {
      e.printStackTrace();
    }
    try {
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Exception thrown by the method starts here
    File file = CoreFiles.getFileOrResource(plugin.getDataFolder().getPath(), "config.yml");
    YamlConfiguration yaml = new YamlConfiguration();
    yaml.load(file);
    yaml.setDefaults(defaults);
    yaml.options().copyDefaults(true);
    yaml.save(file);
    return GuidoConfiguration.load(yaml);
  }

  @NonNull
  public ModuleSettings getModulesSettings(@NonNull GuidoModule module) {
    for (ModuleSettings settings : this.getModulesSettings()) {
      if (settings.getName().equalsIgnoreCase(module.getName())) return settings;
    }
    return new ModuleSettings(module.getName(), new HashMap<>(), null);
  }
}
