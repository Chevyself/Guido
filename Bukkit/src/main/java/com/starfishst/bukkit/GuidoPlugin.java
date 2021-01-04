package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.dependencies.DependencyManager;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.commands.ConfigurationCommands;
import com.starfishst.bukkit.commands.FlyCommand;
import com.starfishst.bukkit.commands.GameModeCommand;
import com.starfishst.bukkit.commands.KnockbackCommand;
import com.starfishst.bukkit.commands.PingCommand;
import com.starfishst.bukkit.commands.TeleportCommand;
import com.starfishst.bukkit.commands.TestCommands;
import com.starfishst.bukkit.commands.providers.GameModeProvider;
import com.starfishst.bukkit.configuration.GuidoConfiguration;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.dependencies.GuidoDependencies;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import com.starfishst.bukkit.listeners.HandlerRegistry;
import com.starfishst.bukkit.utils.FilesUtils;
import com.starfishst.core.providers.type.IContextualProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.Lots;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/** Guido implementation for Bukkit */
public class GuidoPlugin extends JavaPlugin {

  /** Whether the plugin was set to use in the api */
  private final boolean set = Guido.setPlugin(this);

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
          new KnockbackCommand(),
          new PingCommand(),
          new TeleportCommand(),
          new TestCommands());
  /**
   * The dependencies that the plugin can use. Those are soft dependencies meaning that it can run
   * without them. The boolean is whether they are active
   */
  @NonNull @Getter private final DependencyManager dependencies = new GuidoDependencies(this);
  /** Manages the handlers used in the plugin */
  @NonNull @Getter private final HandlerRegistry handlerRegistry = new HandlerRegistry(this);
  /** The client that the plugin is using */
  @NonNull @Getter
  private final BukkitClient client = new BukkitClient("none", "167.114.49.251", 3000);
  /** The guidoConfiguration that the implementation is using */
  @NonNull @Getter private Configuration configuration = new GuidoConfiguration();

  /** Unregisters the commands registered by the implementation */
  private void unregisterCommands() {
    for (GuidoCommand command : this.commands) {
      command.setEnabled(false);
      for (AnnotatedCommand annotated : this.manager.getCommands()) {
        annotated.unregister(Bukkit.getCommandMap());
      }
      this.manager.getCommands().removeIf(annotated -> annotated.getClazz().equals(command));
    }
  }

  /** Register the commands of the bot */
  private void registerCommands() {
    this.manager.getRegistry().addProvider(new GameModeProvider());
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
      File file = FilesUtils.getFileOrResource(this, "config.yml");
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
    this.getClient().setToken(this.configuration.getToken());
    try {
      this.getClient().startConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Check the dependencies and add the listeners to them */
  private void checkDependencies() {
    this.dependencies.checkDependencies();
    for (Dependency dependency : this.dependencies.getDependencies()) {
      if (dependency.isEnabled()) {
        this.commands.addAll(dependency.getCommands());
        for (IContextualProvider<?, CommandContext> provider : dependency.getProviders()) {
          this.manager.getRegistry().addProvider(provider);
        }
      }
    }
  }

  @Override
  public void onDisable() {
    this.unregisterCommands();
    this.handlerRegistry.unregister();
    this.client.disconnect();
    Guido.setPlugin(null);
    super.onDisable();
  }

  @Override
  public void onEnable() {
    this.checkDependencies();
    this.loadConfiguration();
    this.registerCommands();
    this.handlerRegistry.register();
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
