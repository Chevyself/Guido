package com.starfishst.bungee.core;

import com.starfishst.bungee.CommandManager;
import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.configuration.BungeeConfiguration;
import com.starfishst.bungee.api.configuration.GuidoServer;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.core.client.BungeeClient;
import com.starfishst.bungee.core.commands.GuidoCommands;
import com.starfishst.bungee.core.commands.LinkCommand;
import com.starfishst.bungee.core.commands.PermissionCommands;
import com.starfishst.bungee.core.commands.PunishmentCommands;
import com.starfishst.bungee.core.commands.ServerCommands;
import com.starfishst.bungee.core.commands.StatsCommand;
import com.starfishst.bungee.core.commands.providers.GuidoProvidersRegistry;
import com.starfishst.bungee.core.configuration.GuidoBungeeConfiguration;
import com.starfishst.bungee.core.lang.BungeeLanguageHandler;
import com.starfishst.bungee.core.listeners.GroupListener;
import com.starfishst.bungee.core.listeners.MinecraftDataListener;
import com.starfishst.bungee.core.listeners.MotdListener;
import com.starfishst.bungee.core.listeners.PermissionsListener;
import com.starfishst.bungee.core.listeners.PunishmentsListener;
import com.starfishst.bungee.core.listeners.TipsListener;
import com.starfishst.bungee.core.utility.Proxy;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commons.CoreFiles;
import me.googas.commons.Lots;
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
  @NonNull @Getter
  private final CommandManager manager =
      new CommandManager(
          this, this.languageHandler, new GuidoProvidersRegistry(this.languageHandler));
  /** The listeners being used by the plugin */
  @NonNull @Getter
  private final List<GuidoListener> listeners =
      Lots.list(
          this.languageHandler,
          new GroupListener(),
          new MinecraftDataListener(),
          new MotdListener(),
          new PermissionsListener(),
          new PunishmentsListener(),
          new TipsListener());
  /** The client connected with the bot */
  @NonNull @Getter private final BungeeClient client = new BungeeClient("0", this);
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
   * @param <T> the type of listener to get
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
    Guido.setPlugin(this);

    this.loadConfiguration();
    this.client.startTask().setToken(this.configuration.getToken());
    try {
      this.client.startConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (GuidoListener listener : this.listeners) {
      listener.register(this);
      listener.onEnable();
    }
    this.manager.registerCommand(new GuidoCommands());
    this.manager.registerCommand(new LinkCommand());
    this.manager.registerCommand(new PermissionCommands());
    this.manager.registerCommand(new PunishmentCommands());
    this.manager.registerCommand(new ServerCommands());
    this.manager.registerCommand(new StatsCommand());
    this.loadServers();
    super.onEnable();
  }
}
