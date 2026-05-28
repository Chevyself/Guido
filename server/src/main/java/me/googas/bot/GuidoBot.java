package me.googas.bot;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.CommandManagerBuilder;
import com.github.chevyself.starbox.jda.JdaAdapter;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.registry.MiddlewareRegistry;
import com.github.chevyself.starbox.registry.ProvidersRegistry;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.IOUtil;
import me.googas.api.API;
import me.googas.api.GuidoCatchable;
import me.googas.api.GuidoInstance;
import me.googas.api.loader.Loader;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.server.receptors.*;
import me.googas.bot.api.Guido;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.commands.*;
import me.googas.bot.core.commands.administrative.*;
import me.googas.bot.core.commands.providers.*;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.receptors.BungeeConnectionReceptors;
import me.googas.bungee.receptors.BungeeMessagingReceptors;
import me.googas.bungee.receptors.BungeeQueueReceptors;
import me.googas.bungee.receptors.BungeeReceptors;
import me.googas.net.cache.Catchable;
import me.googas.net.cache.MemoryCache;
import me.googas.net.sockets.json.server.JsonSocketServer;
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
public class GuidoBot implements GuidoInstance {

  @NonNull @Getter
  private static final Formatter formatter =
      new CustomFormatter(
          "[%level%] %day%/%month%/%year% [GuidoBot] %hour%:%minute%:%second%: %message% %stack%");

  @NonNull @Getter
  public static final Logger log =
      LoggerFactory.start(
          "GuidoBungee", false, LoggerFactory.createConsoleHandler(GuidoBot.formatter));

  @NonNull @Getter private final API.Messenger messenger = new GuidoMessenger();
  @NonNull @Getter private final MemoryCache cache = new MemoryCache();
  @NonNull @Getter private final GuidoJdaConnection connection = new GuidoJdaConnection();
  @NonNull @Getter private final ListenerManager listenerManager = new ListenerManager();
  @NonNull @Getter private final Scheduler scheduler = new TimerScheduler(new Timer());
  @NonNull @Getter private final GuidoHandlerRegistry handlerRegistry = new GuidoHandlerRegistry();
  // TODO what's up with this class with the new authenticator
  @NonNull @Getter @Setter private BotServer server = new GuidoFallbackServer();
  @Setter @Getter private CommandManager<CommandContext, JdaCommand> commandManager;

  /**
   * The main method of the bot. x
   *
   * <p>Arguments include:
   *
   * <p>'token' for the token of the bot
   *
   * <p>'prefix' the prefix for the commands
   *
   * <p>'loader' waits for a data loader but is none is provided the default json is used
   *
   * <p>For the mongo loader you need these two:
   *
   * <p>'uri' the connection uri for mongo
   *
   * <p>'database' the database to use
   *
   * <p>'port' the port of the socket
   *
   * <p>'timeout' the time out for requests
   *
   * @param args the desired arguments for the bot
   */
  public static void main(String[] args) {
    // Initialize the bot first
    GuidoBot bot = new GuidoBot();
    API.setInstance(bot);
    Guido.setInstance(bot);
    try {
      GuidoBot.log.addHandler(
          LoggerFactory.createFileHandler(
              GuidoBot.getFormatter(),
              IOUtil.currentDirectory() + "/logs/",
              System.currentTimeMillis() + ".txt"));
    } catch (IOException ioException) {
      GuidoBot.log.info("File Handler for logger could not be added");
    }
    ProgramArguments arguments = ProgramArguments.construct(args);
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, exception) -> GuidoBot.log.log(Level.SEVERE, exception, () -> ""));
    Time time = Time.of(1, Unit.SECONDS);
    bot.getScheduler().repeat(time, time, bot.getCache());
    JDA jda = bot.getConnection().createConnection(arguments.getProperty("token", "none"));
    jda.setEventManager(new AnnotatedEventManager());
    GuidoHandlerRegistry registry = bot.getHandlerRegistry();
    registry.setupDiscordLoader().setupLoader(arguments).register(jda);
    MiddlewareRegistry<CommandContext> middlewareRegistry =
        new MiddlewareRegistry<CommandContext>()
            .addGlobalMiddleware(
                new GuidoPermissionChecker(
                    registry.getLanguageHandler(),
                    registry.getLoader(),
                    registry.getDiscordLoader()));
    ProvidersRegistry<CommandContext> providersRegistry =
        new ProvidersRegistry<CommandContext>()
            .addProviders(
                new AuthLevelProvider(),
                new DiscordLinkableProvider(),
                new GroupProvider(),
                new GuidoUserProvider(),
                new GuildDataProvider(),
                new LadderProvider(),
                new LinkableArrayProvider(),
                new LinkableProvider(),
                new LocaleFileProvider(),
                new MatchProvider(),
                new MinecraftLinkableProvider(),
                new MultipleTeamProvider(),
                new UserDataSenderProvider(),
                new UserDataSenderProvider());
    CommandManager<CommandContext, JdaCommand> commandManager =
        new CommandManagerBuilder<>(new JdaAdapter(jda, new GuidoListenerOptions()))
            .setMessagesProvider(registry.getLanguageHandler())
            .setMiddlewareRegistry(middlewareRegistry)
            .setProvidersRegistry(providersRegistry)
            .setCommandMetadataParser(new GuidoMetadataParser())
            .build();
    commandManager.parseAndRegisterAll(
        new AdministrationCommands(),
        new CacheCommands(),
        new CategoryCommands(),
        new ChannelCommands(),
        new EvalCommand(),
        new StopCommand(),
        new VoiceChannelCommands(),
        new HelpCommand(),
        new GroupManagementCommands(),
        new LadderCommands(),
        new LangCommands(),
        new LeaderboardCommands(),
        new MatchCommands(),
        new QueueCommands(),
        new RangesCommand(),
        new SeasonCommands(),
        new TeamCommands(),
        new TokenCommands(),
        new UserCommands());
    bot.setCommandManager(commandManager);
    JsonSocketServer server = GuidoBot.createServer(arguments, bot);
    if (server != null) {
      for (GuidoHandler handler : registry.getRegistered()) {
        if (handler.hasReceptors()) server.addReceptors(handler);
      }
    }
    GuidoBot.log.info("Bot is ready to use");
  }

  /**
   * Creates the server and the receptors
   *
   * @param args the map to getId the port and timeout of the server
   */
  public static JsonSocketServer createServer(
      @NonNull ProgramArguments args, @NonNull GuidoBot bot) {
    try {
      int port = Integer.parseInt(args.getProperty("port", "3000"));
      long timeout = Long.parseLong(args.getProperty("timeout", "3000"));
      Loader loader = bot.getLoader();
      GuidoAuthenticator authenticator =
          new GuidoAuthenticator(bot.getHandlerRegistry().getLoader());
      JsonSocketServer.ServerBuilder serverBuilder =
          JsonSocketServer.listen(port)
              .maxWait(timeout)
              .addReceptors(
                  new BankReceptors(),
                  new GroupReceptors(loader.getGroups()),
                  new GuidoServerReceptors(authenticator),
                  new LinkReceptors(loader.getLinks()),
                  new MatchReceptors(loader.getMatches()),
                  new PunishmentReceptors(loader.getPunishments()),
                  authenticator);
      if (GuidoBungee.isBungee()) {
        serverBuilder.addReceptors(
            new BungeeConnectionReceptors(),
            new BungeeMessagingReceptors(),
            new BungeeQueueReceptors(),
            new BungeeReceptors());
      }
      return serverBuilder.start();
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  @NonNull
  public GuidoBot clearCache() {
    for (SoftReference<Catchable> reference : this.cache.keySetCopy()) {
      Catchable catchable = reference.get();
      if (catchable instanceof GuidoCatchable) {
        try {
          ((GuidoCatchable) catchable).unload(true);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
    this.cache.getMap().clear();
    return this;
  }

  /** CLoses the bot server */
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
  public @NonNull Loader getLoader() {
    return this.handlerRegistry.getLoader();
  }
}
