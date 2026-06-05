package me.googas.bot;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.jda.JdaAdapter;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.registry.MiddlewareRegistry;
import com.github.chevyself.starbox.registry.ProvidersRegistry;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.links.LinkableMatcher;
import me.googas.api.loader.Loader;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.server.receptors.*;
import me.googas.api.stats.StatsProvider;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.*;
import me.googas.bot.core.commands.administrative.*;
import me.googas.bot.core.commands.providers.*;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.ranks.RanksProvider;
import me.googas.bot.core.loader.GuidoFallbackLoader;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.net.api.Server;
import me.googas.net.cache.MemoryCache;
import me.googas.net.sockets.json.server.JsonClientThread;
import me.googas.net.sockets.json.server.JsonSocketServer;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.logging.CustomFormatter;
import me.googas.starbox.logging.LoggerFactory;
import me.googas.starbox.scheduler.Scheduler;
import me.googas.starbox.scheduler.TimerScheduler;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

/** The match making bot */
public class GuidoBot implements GuidoBotRuntime {

  @NonNull @Getter private static final Formatter formatter = new CustomFormatter();

  @NonNull @Getter
  public static final Logger log =
      LoggerFactory.start(
          "GuidoBot", false, LoggerFactory.createConsoleHandler(GuidoBot.formatter));

  @NonNull @Getter private final MemoryCache cache = new MemoryCache();
  @NonNull @Getter private final GuidoJdaConnection connection = new GuidoJdaConnection();
  @NonNull @Getter private final ListenerManager listenerManager = new ListenerManager();
  @NonNull @Getter private final Scheduler scheduler = new TimerScheduler(new Timer());
  // TODO what's up with this class with the new authenticator
  @NonNull @Getter private Server<JsonClientThread> server = new GuidoFallbackServer();

  @Setter @Getter private CommandManager<CommandContext, JdaCommand> commandManager;
  @NonNull private final GuidoServerRuntime parentRuntime;
  @NonNull @Getter private final GuidoHandlerRegistry handlerRegistry;
  @NonNull @Getter private GuidoAuthenticator authenticator;

  public GuidoBot(@NonNull GuidoServerRuntime parentRuntime) {
    this.parentRuntime = parentRuntime;
    this.handlerRegistry = new GuidoHandlerRegistry(this);
    this.authenticator = new GuidoAuthenticator(new GuidoFallbackLoader());
  }

  private void setupLogger() {
    try {
      GuidoBot.log.addHandler(
          LoggerFactory.createFileHandler(
              GuidoBot.getFormatter(),
              parentRuntime.currentDirectory() + "/logs/",
              System.currentTimeMillis() + ".txt"));
    } catch (IOException ioException) {
      GuidoBot.log.info("File Handler for logger could not be added");
    }
  }

  public void start() {
    Guido.setInstance(this);

    this.setupLogger();

    ProgramArguments arguments = parentRuntime.getArguments();
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, exception) -> GuidoBot.log.log(Level.SEVERE, exception, () -> ""));
    Time time = Time.of(1, Unit.SECONDS);
    this.getScheduler().repeat(time, time, this.getCache());
    JDA jda = this.getConnection().createConnection(arguments.getProperty("token", "none"));
    jda.setEventManager(new AnnotatedEventManager());
    GuidoHandlerRegistry registry = this.getHandlerRegistry();
    registry.setupLoader(arguments).register(jda);
    MiddlewareRegistry<CommandContext> middlewareRegistry =
        new MiddlewareRegistry<CommandContext>()
            .addGlobalMiddleware(
                new GuidoPermissionChecker(registry.getLanguageHandler(), registry.getLoader()));
    ProvidersRegistry<CommandContext> providersRegistry =
        new ProvidersRegistry<CommandContext>()
            .addProviders(
                new AuthLevelProvider(),
                new DiscordLinkableProvider(this),
                new GuidoBotRuntimeProvider(this),
                new GuildDataProvider(this),
                new LadderProvider(this),
                new LinkableArrayProvider(),
                new LinkableProvider(),
                new LocaleFileProvider(),
                new MinecraftLinkableProvider(this),
                new MinecraftMatchProvider(this),
                new MinecraftTeamSelectionTypeProvider(),
                new UserDataProvider(this),
                new UserDataSenderProvider(this));
    CommandManager<CommandContext, JdaCommand> commandManager =
        new CommandManagerBuilder<>(new JdaAdapter(jda, new GuidoListenerOptions(), false))
            .setMessagesProvider(registry.getLanguageHandler())
            .setMiddlewareRegistry(middlewareRegistry)
            .setProvidersRegistry(providersRegistry)
            .setCommandMetadataParser(new GuidoMetadataParser())
            .build();
    commandManager.parseAndRegisterAll(
        // new EvalCommand(),TODO engine was removed
        new StopCommand(),
        new HelpCommand(),
        new LadderCommands(),
        new LangCommands(),
        new LeaderboardCommands(),
        new MatchCommands(),
        new QueueCommands(),
        new RangesCommand(),
        new TeamCommands(),
        new TokenCommands(),
        new UserCommands());
    this.setCommandManager(commandManager);
    JsonSocketServer server = createServer(arguments);
    if (server != null) {
      for (GuidoHandler handler : registry.getRegistered()) {
        if (handler.hasReceptors()) server.addReceptors(handler);
      }
      this.server = server;
    }
    GuidoBot.log.info("Bot is ready to use");
  }

  /**
   * Creates the server and the receptors
   *
   * @param args the map to getId the port and timeout of the server
   */
  private JsonSocketServer createServer(@NonNull ProgramArguments args) {
    try {
      int port = Integer.parseInt(args.getProperty("port", "3000"));
      long timeout = Long.parseLong(args.getProperty("timeout", "3000"));
      Loader loader = getLoader();
      this.authenticator = new GuidoAuthenticator(getLoader());
      JsonSocketServer.ServerBuilder serverBuilder =
          JsonSocketServer.listen(port)
              .maxWait(timeout)
              .addReceptors(new GuidoServerReceptors(this.authenticator), this.authenticator);
      return serverBuilder.start();
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** Closes the bot server */
  @NonNull
  public GuidoBot closeServer() {
    try {
      this.server.close();
    } catch (IOException e) {
      GuidoBot.log.log(Level.SEVERE, e, null);
    }
    return this;
  }

  /** Stops the bot */
  public boolean stop() {
    this.handlerRegistry.unregister();
    JDA jda = this.connection.getJda();
    if (jda != null) {
      jda.shutdown();
    }
    return true;
  }

  @Override
  public @NonNull BotJdaProvider getBotJda() {
    return null;
  }

  @Override
  public @NonNull LinkableMatcher getLinkableMatcher() {
    return null;
  }

  @Override
  public me.googas.api.matches.ladder.@NonNull LadderProvider getLadderProvider() {
    return null;
  }

  @Override
  public @NonNull RanksProvider getRanksProvider() {
    return null;
  }

  @Override
  public @NonNull StatsProvider getStatsProvider() {
    return null;
  }

  @Override
  public @NonNull GuidoLoader getLoader() {
    return null;
  }

  @Override
  public @NonNull ListenerManager getListeners() {
    return null;
  }

  @Override
  public @NonNull ProgramArguments getArguments() {
    return null;
  }

  @Override
  public @NonNull File currentDirectory() {
    return null;
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return null;
  }
}
