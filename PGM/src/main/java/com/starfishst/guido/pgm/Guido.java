package com.starfishst.guido.pgm;

import com.starfishst.bukkit.AnnotatedCommand;
import com.starfishst.bukkit.CommandManager;
import com.starfishst.bukkit.CommandManagerOptions;
import com.starfishst.bukkit.messages.DefaultMessagesProvider;
import com.starfishst.bukkit.utils.FilesUtils;
import com.starfishst.core.fallback.Fallback;
import com.starfishst.core.providers.registry.ProvidersRegistry;
import com.starfishst.core.utils.Lots;
import com.starfishst.guido.implementations.Implementation;
import com.starfishst.guido.pgm.commands.FlyCommand;
import com.starfishst.guido.pgm.commands.GuidoCommand;
import com.starfishst.guido.pgm.commands.PingCommand;
import com.starfishst.guido.pgm.configuration.Configuration;
import java.io.IOException;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/** Guido implementation for Sportpaper and PGM */
public class Guido extends JavaPlugin implements Implementation {

  /** The command manager that the implementation is using to register commands */
  @NotNull
  private final CommandManager manager =
      new CommandManager(
          this,
          new CommandManagerOptions(false),
          new DefaultMessagesProvider(),
          new ProvidersRegistry<>());
  /** The set of commands that the implementation is using */
  @NotNull private final Set<GuidoCommand> commands = Lots.set(new FlyCommand(), new PingCommand());
  /** The configuration that the implementation is using */
  @NotNull private Configuration configuration = new Configuration();

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

  /** Load the config.yml. This can be used also to reload the configuration */
  private void loadConfiguration() {
    try {
      YamlConfiguration configuration = new YamlConfiguration();
      configuration.load(FilesUtils.getFileOrResource(this, "config.yml"));
      this.configuration = new Configuration(configuration);
    } catch (IOException | InvalidConfigurationException e) {
      Fallback.addError("IOException: configuration could not be loaded");
      e.printStackTrace();
    }
  }

  @Override
  public void onDisable() {
    this.unregisterCommands();
    super.onDisable();
  }

  @Override
  public void onEnable() {
    this.loadConfiguration();
    this.registerCommands();
    super.onEnable();
  }
}
