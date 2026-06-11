package dev.xevy.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.starbox.CoreFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/** The configuration for the pgm implementation */
public class GuidoConfiguration {

  @NonNull private static final String CONFIG_NAME = "config.yml";

  /** The context in which the server is on */
  @NonNull @Getter private final String context;
  /** The token to connect with the bot */
  @NonNull @Getter private final String token;
  /** The host to connect with the bot */
  @NonNull @Getter private final String host;

  @NonNull @Getter private final String botArguments;
  /** The port to connect with the bot */
  @Getter private final int port;

  @NonNull @Getter private final GuidoBukkitBotConfiguration botConfiguration;
  /** The set of enabled commands */
  @NonNull @Getter private final List<String> commands;
  /** The listener settings of the bot */
  @NonNull @Getter private final List<ModuleSettings> modulesSettings;

  public GuidoConfiguration(
      @NonNull String context,
      @NonNull String token,
      @NonNull String host,
      @NonNull String botArguments,
      int port,
      @NonNull GuidoBukkitBotConfiguration botConfiguration,
      @NonNull List<String> commands,
      @NonNull List<ModuleSettings> modulesSettings) {
    this.context = context;
    this.token = token;
    this.host = host;
    this.botArguments = botArguments;
    this.port = port;
    this.botConfiguration = botConfiguration;
    this.commands = commands;
    this.modulesSettings = modulesSettings;
  }

  public GuidoConfiguration() {
    this(
        "",
        "",
        "localhost",
        "",
        3366,
        new GuidoBukkitBotConfiguration(),
        new ArrayList<>(),
        new ArrayList<>());
  }

  @NonNull
  public static GuidoConfiguration load(ConfigurationSection section) {
    if (section == null) return new GuidoConfiguration();
    int port = section.getInt("port", 3366);
    return new GuidoConfiguration(
        section.getString("context", "Bukkit"),
        section.getString("token", "none"),
        section.getString("host", "localhost"),
        section.getString("bot-arguments", ""),
        port,
        new GuidoBukkitBotConfiguration(
            section.getString("bot.mongo-uri", "mongodb://localhost:27017"),
            section.getString("bot.mongo-database", "guido"),
            section.getString("bot.token", "https://discord.com/developers/"),
            port,
            section.getLong("bot.timeout", 10000),
            section.getLong("bot.guild", 1511402659767128291L)),
        section.getStringList("commands"),
        ModuleSettings.loadAll(section.getConfigurationSection("modules")));
  }

  @NonNull
  public static GuidoConfiguration load(@NonNull Plugin plugin)
      throws IOException, InvalidConfigurationException {
    InputStream resource = plugin.getResource(CONFIG_NAME);
    if (resource == null)
      throw new IllegalArgumentException(
          plugin + " does not have the resource `" + CONFIG_NAME + "`");
    try (InputStreamReader reader = new InputStreamReader(resource)) {
      YamlConfiguration defaults = new YamlConfiguration();
      defaults.load(reader);
      File file =
          CoreFiles.getFileOrResource(
              plugin.getDataFolder().getPath(), "config.yml", plugin.getResource("config.yml"));
      YamlConfiguration yaml = new YamlConfiguration();
      yaml.load(file);
      yaml.setDefaults(defaults);
      yaml.options().copyDefaults(true);
      yaml.save(file);
      return GuidoConfiguration.load(yaml);
    }
  }

  @NonNull
  public ModuleSettings getModulesSettings(@NonNull GuidoModule module) {
    for (ModuleSettings settings : this.getModulesSettings()) {
      if (settings.getName().equalsIgnoreCase(module.getName())) return settings;
    }
    return new ModuleSettings(module.getName(), new HashMap<>(), null);
  }
}
