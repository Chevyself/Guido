package me.googas.bungee;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.bungee.BungeeAdapter;
import com.github.chevyself.starbox.bungee.commands.BungeeCommand;
import com.github.chevyself.starbox.bungee.context.CommandContext;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.bot.GuidoBot;
import me.googas.bot.core.commands.providers.*;
import me.googas.bungee.commands.GuidoCommands;
import me.googas.bungee.commands.LinkCommand;
import me.googas.bungee.commands.PermissionCommands;
import me.googas.bungee.commands.PunishmentCommands;
import me.googas.bungee.commands.ServerCommands;
import me.googas.bungee.commands.StatsCommand;
import me.googas.bungee.configuration.BungeeConfiguration;
import me.googas.bungee.configuration.GuidoBungeeConfiguration;
import me.googas.bungee.configuration.GuidoServer;
import me.googas.bungee.events.GuidoListener;
import me.googas.bungee.lang.BungeeLanguageHandler;
import me.googas.bungee.listeners.MinecraftDataListener;
import me.googas.bungee.listeners.MotdListener;
import me.googas.bungee.listeners.PermissionsListener;
import me.googas.bungee.listeners.PunishmentsListener;
import me.googas.bungee.listeners.TipsListener;
import me.googas.bungee.utility.Proxy;
import me.googas.starbox.CoreFiles;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/** The guido plugin for Bungee */
public class GuidoPlugin extends Plugin {

  // @NonNull @Getter private final Scheduler scheduler = new BungeeScheduler(this);
  /** The bungee language handler */
  @NonNull @Getter
  private final BungeeLanguageHandler languageHandler =
      new BungeeLanguageHandler().loadResources(this, "en");

  /** The command manager */
  @Getter
  private final @NonNull CommandManager<CommandContext, BungeeCommand> manager =
      new CommandManagerBuilder<>(new BungeeAdapter(this))
          .setMessagesProvider(this.languageHandler)
          .build();
  /** The listeners being used by the plugin */
  @NonNull @Getter
  private final List<GuidoListener> listeners =
      Lots.list(
          this.languageHandler,
          new MinecraftDataListener(),
          new MotdListener(),
          new PermissionsListener(),
          new PunishmentsListener(),
          new TipsListener());
  /** The bungeeConfiguration that the plugin will use */
  @NonNull @Getter private BungeeConfiguration configuration = new GuidoBungeeConfiguration();

  /** Loads the configuration */
  public void loadConfiguration() {
    this.getLogger().info("Loading configuration");
    File dataFolder = this.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }
    try {
      File file =
          CoreFiles.getFileOrResource(
              dataFolder.getPath() + "/config.yml", this.getResourceAsStream("config.yml"));
      this.configuration =
          new GuidoBungeeConfiguration(
              ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
    } catch (IOException e) {
      // Fallback.addError("IOException: config.yml could not be loaded");
      e.printStackTrace();
    }
  }

  /** Loads the servers that can be connected */
  public void loadServers() {
    ProxyServer proxy = this.getProxy();
    List<GuidoServer> servers = this.configuration.getServers();
    Proxy.unloadServers(servers);
    for (GuidoServer server : servers) {
      if (proxy.getServerInfo(server.getName()) == null) {
        InetSocketAddress address = server.constructAddress();
        ServerInfo serverInfo =
            proxy.constructServerInfo(server.getName(), address, "Ignored", server.isRestricted());
        proxy.getServers().put(server.getName(), serverInfo);
      }
    }
  }

  /**
   * Get a loaded listener by its class
   *
   * @param clazz the class to match
   * @param <T> the type of listener to getId
   * @return the listener
   */
  public <T extends GuidoListener> T getListener(@NonNull Class<T> clazz) {
    for (GuidoListener listener : this.listeners) {
      if (clazz.isAssignableFrom(listener.getClass())) {
        return clazz.cast(listener);
      }
    }
    throw new IllegalStateException(
        "The listener " + clazz.getSimpleName() + " has not been loaded");
  }

  @Override
  public void onEnable() {
    GuidoBungee.setPlugin(this);
    this.loadConfiguration();
    this.getProxy()
        .getScheduler()
        .runAsync(this, () -> GuidoBot.main(this.configuration.getBotArguments().split(" ")));
    for (GuidoListener listener : this.listeners) {
      listener.register(this);
      listener.onEnable();
    }
    this.manager.parseAndRegisterAll(new GuidoCommands());
    this.manager.parseAndRegisterAll(new LinkCommand());
    this.manager.parseAndRegisterAll(new PermissionCommands());
    this.manager.parseAndRegisterAll(new PunishmentCommands());
    this.manager.parseAndRegisterAll(new ServerCommands());
    this.manager.parseAndRegisterAll(new StatsCommand());
    this.loadServers();
    super.onEnable();
  }
}
