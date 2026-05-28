package com.starfishst.bukkit;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.bukkit.BukkitAdapter;
import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.commands.ConfigurationCommands;
import com.starfishst.bukkit.commands.FlyCommand;
import com.starfishst.bukkit.commands.GameModeCommand;
import com.starfishst.bukkit.commands.GuidoCommand;
import com.starfishst.bukkit.commands.SudoCommand;
import com.starfishst.bukkit.commands.TeleportCommand;
import com.starfishst.bukkit.commands.TestCommands;
import com.starfishst.bukkit.configuration.GuidoConfiguration;
import com.starfishst.bukkit.dependencies.GuidoCompatibilities;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import java.io.IOException;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.starbox.BukkitYamlLanguage;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.ModuleRegistry;
import me.googas.starbox.modules.language.LanguageModule;
import me.googas.starbox.scheduler.Scheduler;
import me.googas.starbox.time.StarboxBukkitScheduler;
import org.bukkit.configuration.InvalidConfigurationException;
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
  @Getter
  private final @NonNull CommandManager<CommandContext, BukkitCommand> manager =
      new CommandManagerBuilder<>(new BukkitAdapter(this, true))
          .setMessagesProvider(this.bukkitLanguageHandler)
          // TODO add providers registry
          .build();
  /** The set of commands that the implementation is using */
  @NonNull
  private final Set<GuidoCommand> commands =
      Lots.set(
          new ConfigurationCommands(),
          new FlyCommand(),
          new GameModeCommand(),
          new SudoCommand(),
          new TeleportCommand(),
          new TestCommands());
  /**
   * The dependencies that the plugin can use. Those are soft dependencies meaning that it can run
   * without them. The boolean is whether they are active
   */
  @NonNull @Getter private final GuidoCompatibilities compatibilities = new GuidoCompatibilities();
  /** The client that the plugin is using */
  @NonNull @Getter
  private final BukkitClient client = new BukkitClient("none", "66.11.113.47", 3000);
  /** The guidoConfiguration that the implementation is using */
  @NonNull @Getter private GuidoConfiguration configuration = new GuidoConfiguration();
  /** Starbox scheduler */
  @NonNull @Getter private final Scheduler scheduler = new StarboxBukkitScheduler(this);

  /** Register the commands of the bot */
  private void registerCommands() {
    for (GuidoCommand command : this.commands) {
      if (!command.isEnabled()) {
        for (String commandName : this.configuration.getCommands()) {
          if (command.getName().equalsIgnoreCase(commandName)) {
            command.setEnabled(true);
            break;
          }
        }
      }
      if (command.isEnabled()) {
        this.manager.parseAndRegisterAll(command);
      }
    }
  }

  /** Load the config.yml. This can be used also to reload the guidoConfiguration */
  private void loadConfiguration() {
    try {
      this.configuration = GuidoConfiguration.load(this);
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
    this.manager.close();
    this.moduleRegistry.disengage();
    this.client.disconnect();
    Guido.setPlugin(null);
    super.onDisable();
  }

  public void setupStarbox() {
    LanguageModule languageModule =
        Starbox.getModules()
            .get(LanguageModule.class)
            .orElseGet(
                () -> {
                  LanguageModule fallback = new LanguageModule();
                  Starbox.getModules().engage(fallback);
                  return fallback;
                });
    languageModule.register(this, BukkitYamlLanguage.of(this, "en"));
  }

  @Override
  public void onEnable() {
    this.compatibilities.check();
    this.setupStarbox();
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
  public CommandManager<CommandContext, BukkitCommand> getCommandManager() {
    return this.manager;
  }
}
