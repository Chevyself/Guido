package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.commands.ConfigurationCommands;
import com.starfishst.bukkit.commands.FlyCommand;
import com.starfishst.bukkit.commands.GameModeCommand;
import com.starfishst.bukkit.commands.PingCommand;
import com.starfishst.bukkit.commands.TeleportCommand;
import com.starfishst.bukkit.commands.TestCommands;
import com.starfishst.bukkit.configuration.GuidoConfiguration;
import com.starfishst.bukkit.dependencies.GuidoCompatibilities;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import com.starfishst.commands.bukkit.CommandManager;
import com.starfishst.commands.bukkit.CommandManagerOptions;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;
import me.googas.starbox.modules.ModuleRegistry;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/** Guido implementation for Bukkit */
public class GuidoPlugin extends JavaPlugin {

  /** Whether the plugin was set to use in the api */
  private final boolean set = Guido.setPlugin(this);

  @NonNull @Getter private final ModuleRegistry moduleRegistry = new ModuleRegistry(this);

  /** The language handler for localized messages */
  @NonNull @Getter
  private final BukkitLanguageHandler bukkitLanguageHandler =
      new BukkitLanguageHandler().loadResources(this, "en");

  // TODO separate command manager and commands to a class such as handler registry
  /** The command manager that the implementation is using to register commands */
  @NonNull @Getter
  private final CommandManager manager =
      new CommandManager(
          this,
          new CommandManagerOptions(false),
          this.bukkitLanguageHandler,
          new GuidoProvidersRegistry(this.bukkitLanguageHandler));
  /** The set of commands that the implementation is using */
  @NonNull
  private final Set<GuidoCommand> commands =
      Lots.set(
          new ConfigurationCommands(),
          new FlyCommand(),
          new GameModeCommand(),
          new PingCommand(),
          new TeleportCommand(),
          new TestCommands());
  /**
   * The dependencies that the plugin can use. Those are soft dependencies meaning that it can run
   * without them. The boolean is whether they are active
   */
  @NonNull @Getter private final GuidoCompatibilities compatibilities = new GuidoCompatibilities();
  /** The client that the plugin is using */
  @NonNull @Getter
  private final BukkitClient client = new BukkitClient("none", "167.114.49.251", 3000);
  /** The guidoConfiguration that the implementation is using */
  @NonNull @Getter private Configuration configuration = new GuidoConfiguration();

  /** Register the commands of the bot */
  private void registerCommands() {
    for (GuidoCommand command : this.commands) {
      if (!command.isEnabled()) {
        for (String commandName : this.configuration.getEnabledCommands()) {
          if (command.getName().equalsIgnoreCase(commandName)) {
            command.setEnabled(true);
            break;
          }
        }
      }
      if (command.isEnabled()) {
        this.manager.registerCommand(command);
      }
    }
    this.manager.registerPlugin();
  }

  /** Load the config.yml. This can be used also to reload the guidoConfiguration */
  private void loadConfiguration() {
    try {
      InputStreamReader reader = new InputStreamReader(this.getResource("config.yml"));
      YamlConfiguration defaults = new YamlConfiguration();
      defaults.load(reader);
      // TODO
      File file = CoreFiles.getFileOrResource(this.getDataFolder().getPath(), "config.yml");
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.load(file);
      configuration.setDefaults(defaults);
      configuration.options().copyDefaults(true);
      configuration.save(file);
      this.configuration = new GuidoConfiguration(configuration);
    } catch (IOException | InvalidConfigurationException e) {
      e.printStackTrace();
    }
  }

  /** Start the connection with the bot */
  private void startConnection() {
    this.getClient().startTask().setToken(this.configuration.getToken());
    try {
      this.getClient().startConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onDisable() {
    this.manager.unregister();
    this.moduleRegistry.disengage();
    this.client.disconnect();
    Guido.setPlugin(null);
    super.onDisable();
  }

  @Override
  public void onEnable() {
    this.compatibilities.check();
    this.loadConfiguration();
    this.registerCommands();
    this.startConnection();
    super.onEnable();
  }

  /**
   * Get the language handler of the plugin
   *
   * @return the language handler
   */
  @NonNull
  public BukkitLanguageHandler getLanguageHandler() {
    return this.bukkitLanguageHandler;
  }

  /**
   * Get the command manager that the bot is using
   *
   * @return the command manager
   */
  @NonNull
  public CommandManager getCommandManager() {
    return this.manager;
  }
}
