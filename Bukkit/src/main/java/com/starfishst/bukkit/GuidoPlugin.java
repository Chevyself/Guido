package com.starfishst.bukkit;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.config.Configuration;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.dependencies.DependencyManager;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.commands.FlyCommand;
import com.starfishst.bukkit.commands.GameModeCommand;
import com.starfishst.bukkit.commands.PingCommand;
import com.starfishst.bukkit.commands.TestCommands;
import com.starfishst.bukkit.commands.providers.GameModeProvider;
import com.starfishst.bukkit.configuration.GuidoConfiguration;
import com.starfishst.bukkit.dependencies.GuidoDependencies;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import com.starfishst.bukkit.listeners.CommandExecutionListener;
import com.starfishst.bukkit.listeners.MatchMakingListener;
import com.starfishst.bukkit.listeners.PermissionListener;
import com.starfishst.bukkit.listeners.TestListener;
import com.starfishst.bukkit.utils.BukkitUtils;
import com.starfishst.bukkit.utils.FilesUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import me.googas.api.client.Client;
import me.googas.commons.Lots;
import me.googas.commons.fallback.Fallback;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Guido implementation for Bukkit */
public class GuidoPlugin extends JavaPlugin {

  /** The language handler for localized messages */
  @NotNull
  private final BukkitLanguageHandler bukkitLanguageHandler =
      new BukkitLanguageHandler().loadResources(this, "en");

  /** The command manager that the implementation is using to register commands */
  @NotNull
  private final CommandManager manager =
      new CommandManager(
          this,
          new CommandManagerOptions(false),
          this.bukkitLanguageHandler,
          new GuidoProvidersRegistry(this.bukkitLanguageHandler));
  /** The set of commands that the implementation is using */
  @NotNull
  private final Set<GuidoCommand> commands =
      Lots.set(new FlyCommand(), new GameModeCommand(), new PingCommand(), new TestCommands());
  /** The listeners that this requires */
  @NotNull private final List<GuidoListener> listeners = Lots.list(this.bukkitLanguageHandler);
  /** The guidoConfiguration that the implementation is using */
  @NotNull private Configuration configuration = new GuidoConfiguration();
  /**
   * The dependencies that the plugin can use. Those are soft dependencies meaning that it can run
   * without them. The boolen is whether they are active
   */
  @NotNull private final DependencyManager dependencies = new GuidoDependencies(this);

  /** The client that the plugin is using */
  @NotNull private final Client client = new Client("none", "104.243.43.175", 3000);

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
      File file = FilesUtils.getFileOrResource(this, "config.yml");
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.load(file);
      configuration.setDefaults(defaults);
      configuration.options().copyDefaults(true);
      configuration.save(file);
      this.configuration = new GuidoConfiguration(configuration);
    } catch (IOException | InvalidConfigurationException e) {
      Fallback.addError("IOException: guidoConfiguration could not be loaded");
      e.printStackTrace();
    }
  }

  /** Unregisters the listeners */
  private void unregisterListeners() {
    for (GuidoListener listener : this.listeners) {
      if (listener.isEnabled()) {
        listener.unregister();
      }
    }
    this.listeners.clear();
  }

  /** Registers the listeners */
  private void registerListeners() {
    this.listeners.addAll(this.getDefaultListeners());
    for (GuidoListener listener : this.listeners) {
      if (listener.isEnabled()) {
        this.getLogger().info(listener.getName() + " has been registered");
        listener.register(this);
      } else {
        this.getLogger().info(listener.getName() + " was not enabled");
      }
    }
  }

  /** Start the connection with the bot */
  private void startConnection() {
    this.getClient().setToken(this.configuration.getToken());
    MatchMakingListener makingListener = this.getListener(MatchMakingListener.class);
    if (makingListener != null) {
      this.getClient().addReceptors(makingListener);
    }
    try {
      this.getClient().startConnection();
    } catch (IOException e) {
      Fallback.addError("Server could not be connected");
      e.printStackTrace();
    }
  }

  /**
   * Get the configuration that the plugin is using
   *
   * @return the plugin configuration
   */
  @NotNull
  public Configuration getConfiguration() {
    return this.configuration;
  }

  /**
   * Get the default listeners that the plugin needs
   *
   * @return the default listeners
   */
  private @NotNull List<GuidoListener> getDefaultListeners() {
    return Lots.list(
        new CommandExecutionListener(), new PermissionListener(this), new TestListener());
  }

  /** Check the dependencies and add the listeners to them */
  private void checkDependencies() {
    this.dependencies.checkDependencies();
    for (Dependency dependency : this.dependencies.getDependencies()) {
      if (dependency.isEnabled()) {
        this.listeners.addAll(dependency.getListeners(this));
      }
    }
  }

  /**
   * Get a listener using its class
   *
   * @param clazz the class of the listener
   * @param <T> the type of the listener class
   * @return the listener if found null if it might not have been registered
   */
  @Nullable
  public <T extends GuidoListener> T getListener(@NotNull Class<T> clazz) {
    for (GuidoListener listener : this.listeners) {
      if (listener.getClass() == clazz) {
        return clazz.cast(listener);
      }
    }
    return null;
  }

  /**
   * Get the dependencies that are connected with the plugin
   *
   * @return the dependencies that the bot has
   */
  @NotNull
  public DependencyManager getDependencies() {
    return this.dependencies;
  }

  public @NotNull Client getClient() {
    return this.client;
  }

  @Override
  public void onDisable() {
    this.unregisterCommands();
    this.unregisterListeners();
    this.client.disconnect();
    Guido.setPlugin(null);
    super.onDisable();
  }

  @Override
  public void onEnable() {
    Guido.setPlugin(this);
    this.checkDependencies();
    this.loadConfiguration();
    this.registerCommands();
    this.registerListeners();
    this.startConnection();
    BukkitUtils.startCache(this);
    super.onEnable();
  }

  /**
   * Get the language handler of the plugin
   *
   * @return the language handler
   */
  @NotNull
  public BukkitLanguageHandler getLanguageHandler() {
    return this.bukkitLanguageHandler;
  }
}
