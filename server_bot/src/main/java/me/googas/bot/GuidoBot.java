package me.googas.bot;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.jda.JdaAdapter;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.registry.MiddlewareRegistry;
import com.github.chevyself.starbox.registry.ProvidersRegistry;
import dev.xevy.guido.mongo.MongoLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.server.receptors.*;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.commands.*;
import me.googas.bot.core.commands.administrative.*;
import me.googas.bot.core.commands.middleware.EmbededResultHandler;
import me.googas.bot.core.commands.providers.*;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.loader.GuidoFallbackLoader;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.net.api.Server;
import me.googas.net.sockets.json.server.JsonClientThread;
import me.googas.net.sockets.json.server.JsonSocketServer;
import me.googas.server.GuidoServerRuntime;
import me.googas.server.loader.GuidoLoader;
import me.googas.starbox.ProgramArguments;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.logging.CustomFormatter;
import me.googas.starbox.logging.LoggerFactory;
import me.googas.starbox.scheduler.Scheduler;
import me.googas.starbox.scheduler.TimerScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

/** The match making bot */
public final class GuidoBot implements GuidoBotRuntime {

  @NonNull @Getter private static final Formatter formatter = new CustomFormatter();

  @NonNull @Getter
  public static final Logger logger =
      LoggerFactory.start(
          "GuidoBot", false, LoggerFactory.createConsoleHandler(GuidoBot.formatter));

  @NonNull private final GuidoServerRuntime parentRuntime;
  @NonNull private final GuidoBotConfig config;
  @NonNull @Getter private final GuidoJdaProvider jdaProvider;
  @NonNull @Getter private GuidoAuthenticator authenticator;

  @NonNull @Getter private final GuidoJdaConnection jdaConnection = new GuidoJdaConnection();
  @NonNull @Getter private final ListenerManager listeners = new ListenerManager();
  @NonNull @Getter private final Scheduler scheduler = new TimerScheduler(new Timer());
  @NonNull @Getter private final GuidoHandlerRegistry handlers = new GuidoHandlerRegistry(this);
  @NonNull @Getter private final GuidoLadderProvider ladderProvider = new GuidoLadderProvider(this);
  @NonNull @Getter private final GuidoRanksProvider ranksProvider = new GuidoRanksProvider(this);
  @NonNull @Getter private final GuidoStatsProvider statsProvider = new GuidoStatsProvider(this);
  @NonNull @Getter private GuidoLoader loader = new GuidoFallbackLoader();
  @NonNull @Getter private Server<JsonClientThread> server = new GuidoFallbackServer();

  private CommandManager<CommandContext, JdaCommand> commandManager;

  public GuidoBot(@NonNull GuidoServerRuntime parentRuntime, @NonNull GuidoBotConfig config) {
    this.parentRuntime = parentRuntime;
    this.config = config;
    this.jdaProvider = new GuidoJdaProvider(this, config.getGuildId());
    this.authenticator = new GuidoAuthenticator(new GuidoFallbackLoader());
  }

  private void setupLogger() {
    try {
      GuidoBot.logger.addHandler(
          LoggerFactory.createFileHandler(
              GuidoBot.getFormatter(),
              parentRuntime.currentDirectory() + "/logs/",
              System.currentTimeMillis() + ".txt"));
    } catch (IOException ioException) {
      GuidoBot.logger.info("File Handler for logger could not be added");
    }

    Thread.setDefaultUncaughtExceptionHandler(
        (thread, exception) -> GuidoBot.logger.log(Level.SEVERE, exception, () -> ""));
  }

  public void setupLoader() {
    try {
      loader = MongoLoader.join(this, config.getMongoUri(), config.getDatabase());
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to setup mongo loader", e);
    }
  }

  @NonNull
  private JDA setupJda() {
    JDA jda = this.jdaConnection.createConnection(config.getDiscordToken());
    jda.setEventManager(new AnnotatedEventManager());
    return jda;
  }

  private void setupSocketServer() {
    JsonSocketServer server = createServer();
    if (server != null) {
      for (GuidoHandler handler : this.handlers.getRegistered()) {
        if (handler.hasReceptors()) server.addReceptors(handler);
      }
      this.server = server;
    }
  }

  private void setupCommands(GuidoHandlerRegistry registry, JDA jda) {
    MiddlewareRegistry<CommandContext> middlewareRegistry =
        new MiddlewareRegistry<CommandContext>()
            .addGlobalMiddlewares(
                new GuidoPermissionChecker(registry.getLanguageHandler(), this.loader),
                new EmbededResultHandler());
    ProvidersRegistry<CommandContext> providersRegistry =
        new ProvidersRegistry<CommandContext>()
            .addProviders(
                new AuthLevelProvider(),
                new DiscordLinkableProvider(this),
                new GuidoBotRuntimeProvider(this),
                new GuildDataProvider(this),
                new LadderArgumentProvider(this),
                new LinkableArrayProvider(),
                new LinkableProvider(),
                new LocaleFileProvider(),
                new MinecraftLinkableProvider(this),
                new MinecraftMatchProvider(this),
                new MinecraftTeamSelectionTypeProvider(),
                new UserDataProvider(this),
                new UserDataSenderProvider(this));
    this.commandManager =
        new CommandManagerBuilder<>(new JdaAdapter(jda, new GuidoListenerOptions(), false))
            .setMessagesProvider(registry.getLanguageHandler())
            .setMiddlewareRegistry(middlewareRegistry)
            .setProvidersRegistry(providersRegistry)
            .setCommandMetadataParser(new GuidoMetadataParser())
            .build();
    commandManager.parseAndRegisterAll(
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
  }

  public void start() {
    Guido.setInstance(this);
    this.setupLogger();
    this.setupLoader();
    JDA jda = this.setupJda();
    GuidoHandlerRegistry registry = this.handlers.register(jda);
    this.setupSocketServer();

    this.setupCommands(registry, jda);

    GuidoBot.logger.info("Bot is ready to use");
  }

  /** Creates the server and the receptors */
  private JsonSocketServer createServer() {
    try {
      int port = config.getServerPort();
      long timeout = config.getTimeout();
      this.authenticator = new GuidoAuthenticator(getLoader());
      JsonSocketServer.ServerBuilder serverBuilder =
          JsonSocketServer.listen(port)
              .maxWait(timeout)
              .addReceptors(new GuidoServerReceptors(this.authenticator), this.authenticator);
      return serverBuilder.start();
    } catch (IOException | NumberFormatException e) {
      logger.log(Level.SEVERE, "Failed to setup socket server", e);
    }
    return null;
  }

  /** Closes the bot server */
  @NonNull
  public GuidoBot closeServer() {
    try {
      this.server.close();
    } catch (IOException e) {
      GuidoBot.logger.log(Level.SEVERE, e, null);
    }
    return this;
  }

  /** Stops the bot */
  @Override
  public void close() throws IOException {
    this.handlers.unregister();
    this.commandManager.close();
    this.jdaConnection.close();
    this.server.close();
    this.loader.close();
  }

  @NonNull
  public CommandManager<CommandContext, JdaCommand> getCommandManager() {
    return Objects.requireNonNull(
        this.commandManager, "Command manager may not have been initialized yet");
  }

  @Override
  public @NonNull GuidoJdaProvider getBotJda() {
    return Objects.requireNonNull(
        this.jdaProvider, "Jda provider may not have been initialized yet");
  }

  @Override
  public @NonNull ProgramArguments getArguments() {
    return this.parentRuntime.getArguments();
  }

  @Override
  public @NonNull File currentDirectory() {
    return this.parentRuntime.currentDirectory();
  }

  @Override
  public @NonNull InputStream getResource(@NonNull String name) {
    return this.parentRuntime.getResource(name);
  }
}
