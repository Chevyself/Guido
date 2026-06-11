package me.googas.bungee;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.bungee.BungeeAdapter;
import com.github.chevyself.starbox.bungee.commands.BungeeCommand;
import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.middleware.BungeeResultHandlingMiddleware;
import com.github.chevyself.starbox.registry.MiddlewareRegistry;
import com.github.chevyself.starbox.registry.ProvidersRegistry;
import dev.xevy.guido.mc.LinkCommand;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.bot.GuidoBot;
import me.googas.bot.api.Guido;
import me.googas.bungee.commands.GuidoCommands;
import me.googas.bungee.commands.ServerCommands;
import me.googas.bungee.commands.providers.BungeeLocaleFileProvider;
import me.googas.bungee.commands.providers.JsonClientProvider;
import me.googas.bungee.commands.providers.MinecraftPlayerProvider;
import me.googas.bungee.commands.providers.MinecraftResultProviderExtraArgumentProvider;
import me.googas.bungee.configuration.BungeeConfiguration;
import me.googas.bungee.configuration.GuidoBungeeConfiguration;
import me.googas.bungee.configuration.GuidoServer;
import me.googas.bungee.events.GuidoListener;
import me.googas.bungee.lang.BungeeLanguageHandler;
import me.googas.bungee.listeners.MinecraftDataListener;
import me.googas.bungee.listeners.MotdListener;
import me.googas.bungee.listeners.TipsListener;
import me.googas.bungee.receptors.BungeeConnectionReceptors;
import me.googas.bungee.receptors.BungeeMessagingReceptors;
import me.googas.bungee.receptors.BungeeQueueReceptors;
import me.googas.bungee.receptors.BungeeReceptors;
import me.googas.bungee.utility.Proxy;
import me.googas.net.api.Server;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.net.sockets.json.server.JsonClientThread;
import me.googas.net.sockets.json.server.JsonSocketServer;
import me.googas.starbox.CoreFiles;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/** The guido plugin for Bungee */
public class GuidoPlugin extends Plugin implements GuidoBungeeRuntime {

  // @NonNull @Getter private final Scheduler scheduler = new BungeeScheduler(this);
  /** The bungee language handler */
  @NonNull @Getter
  private final BungeeLanguageHandler languageHandler =
      new BungeeLanguageHandler().loadResources(this, "en");

  @NonNull
  private final BungeeResultProvider resultProvider = new BungeeResultProvider(languageHandler);

  /** The command manager */
  @Getter
  private final @NonNull CommandManager<CommandContext, BungeeCommand> manager =
      new CommandManagerBuilder<>(new BungeeAdapter(this))
          .setMessagesProvider(this.languageHandler)
          .setMiddlewareRegistry(
              new MiddlewareRegistry<CommandContext>()
                  .addGlobalMiddleware(new BungeeResultHandlingMiddleware()))
          .setProvidersRegistry(
              new ProvidersRegistry<CommandContext>()
                  .addProviders(
                      new BungeeLocaleFileProvider(),
                      new JsonClientProvider(),
                      new MinecraftPlayerProvider(),
                      new MinecraftResultProviderExtraArgumentProvider(resultProvider)))
          .build();
  /** The bungeeConfiguration that the plugin will use */
  @NonNull @Getter private BungeeConfiguration configuration = loadConfiguration();

  /** The listeners being used by the plugin */
  @NonNull @Getter
  private final List<GuidoListener> listeners =
      Lots.list(
          this.languageHandler,
          new MinecraftDataListener(this),
          new MotdListener(this),
          new TipsListener());

  private JsonClient client = null;

  /**
   * Loads the configuration
   *
   * @return
   */
  public GuidoBungeeConfiguration loadConfiguration() {
    this.getLogger().info("Loading configuration");
    File dataFolder = this.getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }
    try {
      File file =
          CoreFiles.getFileOrResource(
              dataFolder.getPath() + "/config.yml", this.getResourceAsStream("config.yml"));
      return new GuidoBungeeConfiguration(
          ConfigurationProvider.getProvider(YamlConfiguration.class).load(file));
    } catch (IOException e) {
      // Fallback.addError("IOException: config.yml could not be loaded");
      e.printStackTrace();
      return new GuidoBungeeConfiguration();
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
    GuidoBungeeConfiguration guidoBungeeConfiguration = this.loadConfiguration();
    new GuidoBot(this, guidoBungeeConfiguration).start();
    Server<JsonClientThread> server = Guido.getServer();
    if (server instanceof JsonSocketServer) {
      ((JsonSocketServer) server)
          .addReceptors(
              new BungeeConnectionReceptors(),
              new BungeeMessagingReceptors(),
              new BungeeQueueReceptors(),
              new BungeeReceptors());
    }
    try {
      client = JsonClient.join("localhost", 3366).start();
    } catch (IOException e) {
      getLogger().severe("Failed to init json client");
    }
    for (GuidoListener listener : this.listeners) {
      listener.register(this);
      listener.onEnable();
    }
    this.manager.parseAndRegisterAll(new GuidoCommands());
    this.manager.parseAndRegisterAll(new LinkCommand());
    this.manager.parseAndRegisterAll(new ServerCommands());
    // TODO implement stats command
    this.loadServers();
    super.onEnable();
  }

  @Override
  public @NonNull JsonClient getClient() {
    return Objects.requireNonNull(client, "Client may not have been initialized yet");
  }

  @Override
  public @NonNull ProgramArguments getArguments() {
    return ProgramArguments.construct(this.configuration.getBotArguments());
  }

  @Override
  public @NonNull File currentDirectory() {
    return this.getDataFolder();
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return Objects.requireNonNull(
        this.getResourceAsStream(name), "Could not find resource " + name);
  }

  @Override
  public void close() throws IOException {
    // No-op
  }

  @Override
  public @NonNull ListenerManager getListeners() {
    throw new UnsupportedOperationException();
  }
}
