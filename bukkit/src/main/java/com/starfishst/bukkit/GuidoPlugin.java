package com.starfishst.bukkit;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.bukkit.BukkitAdapter;
import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.registry.ProvidersRegistry;
import com.starfishst.bukkit.client.BukkitClient;
import com.starfishst.bukkit.commands.FlyCommand;
import com.starfishst.bukkit.commands.GameModeCommand;
import com.starfishst.bukkit.commands.GuidoCommand;
import com.starfishst.bukkit.commands.SudoCommand;
import com.starfishst.bukkit.commands.TeleportCommand;
import com.starfishst.bukkit.commands.TestCommands;
import com.starfishst.bukkit.commands.providers.BukkitLocaleFileProvider;
import com.starfishst.bukkit.commands.providers.GameModeProvider;
import com.starfishst.bukkit.configuration.GuidoConfiguration;
import com.starfishst.bukkit.dependencies.GuidoCompatibilities;
import com.starfishst.bukkit.lang.BukkitLanguageHandler;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.starbox.BukkitYamlLanguage;
import me.googas.starbox.Starbox;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.modules.ModuleRegistry;
import me.googas.starbox.modules.language.LanguageModule;
import me.googas.starbox.scheduler.Scheduler;
import me.googas.starbox.time.StarboxBukkitScheduler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/** Guido implementation for Bukkit */
public class GuidoPlugin extends JavaPlugin implements GuidoBukkitRuntime {

  /** Whether the plugin was set to use in the api */
  private final boolean set = Guido.setPlugin(this);

  @NonNull @Getter private final ModuleRegistry moduleRegistry = new ModuleRegistry(this);

  /** The language handler for localized messages */
  @NonNull @Getter
  private final BukkitLanguageHandler bukkitLanguageHandler =
      new BukkitLanguageHandler().loadResources(this, "en");

  /** The command manager that the implementation is using to register commands */
  @Getter
  private final @NonNull CommandManager<CommandContext, BukkitCommand> commandManager =
      new CommandManagerBuilder<>(new BukkitAdapter(this, true))
          .setMessagesProvider(this.bukkitLanguageHandler)
          .setProvidersRegistry(
              new ProvidersRegistry<CommandContext>()
                  .addProviders(new BukkitLocaleFileProvider(), new GameModeProvider()))
          .build();
  /** The set of commands that the implementation is using */
  @NonNull
  private final Set<GuidoCommand> commands =
      Lots.set(
          new FlyCommand(),
          new GameModeCommand(),
          new SudoCommand(),
          new TeleportCommand(),
          new TestCommands());
  /**
   * The dependencies that the plugin can use. Those are soft dependencies meaning that it can run
   * without them. The boolean is whether they are active
   */
  @NonNull @Getter
  private final GuidoCompatibilities compatibilities = new GuidoCompatibilities(this);
  /** The client that the plugin is using */
  private BukkitClient client;
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
        this.commandManager.parseAndRegisterAll(command);
      }
    }
  }

  /** Load the config.yml. This can be used also to reload the guidoConfiguration */
  private void loadConfiguration() {
    try {
      this.configuration = GuidoConfiguration.load(this);
    } catch (IOException | InvalidConfigurationException e) {
      this.getLogger().log(Level.SEVERE, "Failed to load configuration", e);
    }
  }

  /** Start the connection with the bot */
  private void startConnection() {
    try {
      this.client =
          new BukkitClient(
                  this.configuration.getToken(),
                  this.configuration.getHost(),
                  this.configuration.getPort(),
                  this)
              .startTask();
      this.client
          .startConnection()
          .whenComplete(
              (result, e) -> {
                if (e != null) {
                  this.getLogger().log(Level.SEVERE, "Failed to auth client", e);
                  return;
                }
                this.getLogger().info("Received client auth " + result);
              });
    } catch (IOException e) {
      this.getLogger().log(Level.SEVERE, "Failed to initialize client", e);
    }
  }

  @Override
  public void onDisable() {
    this.commandManager.close();
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
    languageModule.register(this, BukkitYamlLanguage.of(this, "lang/en"));
  }

  @Override
  public void onEnable() {
    this.compatibilities.check().getCompatibilities().stream()
        .filter(Compatibility::isEnabled)
        .forEach(
            compatibility -> {
              this.moduleRegistry.engage(compatibility.getModules(this));
              this.commandManager.registerAll(compatibility.getCommands());
              this.commandManager.getProvidersRegistry().addProviders(compatibility.getProviders());
            });
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

  @Override
  public @NonNull BukkitScheduler getBukkitScheduler() {
    return Bukkit.getScheduler();
  }

  @NonNull
  public BukkitClient getClient() {
    return Objects.requireNonNull(this.client, "Client may not have been initialized yet");
  }

  @Override
  public void sync(@NonNull Runnable runnable) {
    this.getBukkitScheduler().runTask(this, runnable);
  }
}
