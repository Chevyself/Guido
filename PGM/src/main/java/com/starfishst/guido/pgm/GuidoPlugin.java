package com.starfishst.guido.pgm;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.CommandManager;
import com.starfishst.bukkit.CommandManagerOptions;
import com.starfishst.bukkit.messages.DefaultMessagesProvider;
import com.starfishst.bukkit.utils.BukkitUtils;
import com.starfishst.bukkit.utils.FilesUtils;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.providers.registry.ProvidersRegistry;
import com.starfishst.core.utils.Lots;
import com.starfishst.guido.implementations.Implementation;
import com.starfishst.guido.pgm.api.Guido;
import com.starfishst.guido.pgm.api.commands.GuidoCommand;
import com.starfishst.guido.pgm.api.config.Configuration;
import com.starfishst.guido.pgm.api.config.DataLoader;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import com.starfishst.guido.pgm.commands.FlyCommand;
import com.starfishst.guido.pgm.commands.GameModeCommand;
import com.starfishst.guido.pgm.commands.PermissionCommands;
import com.starfishst.guido.pgm.commands.PingCommand;
import com.starfishst.guido.pgm.commands.providers.GameModeProvider;
import com.starfishst.guido.pgm.configuration.GuidoConfiguration;
import com.starfishst.guido.pgm.configuration.GuidoDataLoader;
import com.starfishst.guido.pgm.listeners.CommandExecutionListener;
import com.starfishst.guido.pgm.listeners.PermissionListener;
import com.starfishst.guido.pgm.listeners.TestListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** GuidoPlugin implementation for Sportpaper and PGM */
public class GuidoPlugin extends JavaPlugin implements Implementation {

  /** The command manager that the implementation is using to register commands */
  @NotNull
  private final CommandManager manager =
      new CommandManager(
          this,
          new CommandManagerOptions(false),
          new DefaultMessagesProvider(),
          new ProvidersRegistry<>());
  /** The set of commands that the implementation is using */
  @NotNull
  private final Set<GuidoCommand> commands =
      Lots.set(
          new FlyCommand(), new GameModeCommand(), new PermissionCommands(), new PingCommand());
  /** The data loader for this implementation */
  @NotNull private final DataLoader loader = new GuidoDataLoader(this);
  /** The listeners that this requires */
  @NotNull
  private final List<GuidoListener> listeners =
      Lots.list(
          (GuidoDataLoader) loader,
          new CommandExecutionListener(),
          new PermissionListener(this),
          new TestListener());
  /** The guidoConfiguration that the implementation is using */
  @NotNull private Configuration configuration = new GuidoConfiguration();

  /** Unregisters the commands registered by the implementation */
  private void unregisterCommands() {
    for (GuidoCommand command : this.commands) {
      command.setEnabled(false);
      for (AnnotatedCommand annotated : manager.getCommands()) {
        annotated.unregister(Bukkit.getCommandMap());
      }
      manager.getCommands().removeIf(annotated -> annotated.getClazz().equals(command));
    }
  }

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
        manager.registerCommand(command);
      }
    }
  }

  /** Load the config.yml. This can be used also to reload the guidoConfiguration */
  private void loadConfiguration() {
    try {
      File file = FilesUtils.getFileOrResource(this, "config.yml");
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.load(file);
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
  }

  /** Registers the listeners */
  private void registerListeners() {
    for (GuidoListener listener : this.listeners) {
      if (listener.isEnabled()) {
        listener.register(this);
      }
    }
  }

  /**
   * Get the data loader that the plugin is using
   *
   * @return the data loader
   */
  @NotNull
  public DataLoader getLoader() {
    return loader;
  }

  /**
   * Get the configuration that the plugin is using
   *
   * @return the plugin configuration
   */
  @NotNull
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public void onDisable() {
    this.unregisterCommands();
    this.unregisterListeners();
    Guido.setPlugin(null);
    super.onDisable();
  }

  @Override
  public void onEnable() {
    Guido.setPlugin(this);
    this.loadConfiguration();
    this.registerCommands();
    this.registerListeners();
    this.manager.getRegistry().addProvider(new GameModeProvider());
    BukkitUtils.startCache(this);
    super.onEnable();
  }
}
