package me.googas.bot.core;

import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ManagerOptions;
import java.awt.*;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import lombok.NonNull;
import me.googas.api.loader.DataLoader;
import me.googas.bot.api.events.GuidoCancellable;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.commands.AdministrationCommands;
import me.googas.bot.core.commands.CategoryCommands;
import me.googas.bot.core.commands.ChannelCommands;
import me.googas.bot.core.commands.DeveloperCommands;
import me.googas.bot.core.commands.HelpCommand;
import me.googas.bot.core.commands.LadderCommands;
import me.googas.bot.core.commands.LangCommands;
import me.googas.bot.core.commands.LeaderboardCommands;
import me.googas.bot.core.commands.MatchCommands;
import me.googas.bot.core.commands.QueueCommands;
import me.googas.bot.core.commands.RangesCommand;
import me.googas.bot.core.commands.TeamCommands;
import me.googas.bot.core.commands.TestCommands;
import me.googas.bot.core.commands.TokenCommands;
import me.googas.bot.core.commands.UserCommands;
import me.googas.bot.core.commands.VoiceChannelCommands;
import me.googas.bot.core.commands.permissions.GuidoPermissionChecker;
import me.googas.bot.core.commands.providers.GuidoProvidersRegistry;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.decorations.DecorationsHandler;
import me.googas.bot.core.handlers.lang.GuidoLanguageHandler;
import me.googas.bot.core.handlers.link.LinkHandler;
import me.googas.bot.core.handlers.loader.GuidoFileLoader;
import me.googas.bot.core.handlers.loader.JsongoDataLoader;
import me.googas.bot.core.handlers.matches.MatchCalculator;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.PGMMatchHandler;
import me.googas.bot.core.handlers.matches.QueueHandler;
import me.googas.bot.core.handlers.responsive.GuidoMessagesController;
import me.googas.bot.core.handlers.test.TestHandler;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.bot.core.server.GuidoServer;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import me.googas.commons.cache.Catchable;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.Event;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.log.LoggerFactory;
import me.googas.commons.log.formatters.CustomFormatter;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

/** The match making bot */
public class Guido {

  /** The cache of the bot */
  @NonNull private static final MemoryCache cache = new MemoryCache();

  static {
    CustomFormatter formatter =
        new CustomFormatter(
            "[%level%] %day%/%month%/%year% [Guido] %hour%:%minute%:%second%: %message% %stack%");
    try {
      Guido.logger = LoggerFactory.start("Guido", formatter, "Log-" + System.currentTimeMillis());
    } catch (IOException e) {
      Guido.logger = LoggerFactory.start("Guido", LoggerFactory.getConsoleHandler(formatter));
      Guido.logger.log(Level.SEVERE, e, null);
    }
  }
  /** The timer which can be used in other instances of the bot */
  @NonNull private static final Timer timer = new Timer(Guido.class.getName());
  /** The connection of guido with discord */
  @NonNull private static final GuidoJdaConnection connection = new GuidoJdaConnection();
  /** The listener manager for calling events */
  @NonNull private static final ListenerManager listenerManager = new ListenerManager();
  /** The logger that the bot will be using to display messages */
  @NonNull
  private static Logger logger =
      LoggerFactory.start("Guido", LoggerFactory.getConsoleHandler(new SimpleFormatter()));
  /** The data loader for the bot */
  @NonNull private static BotDataLoader dataLoader = new GuidoFileLoader();

  /** The language handler for the bot */
  @NonNull
  private static final GuidoLanguageHandler languageHandler =
      new GuidoLanguageHandler(Guido.dataLoader);

  /** The list of handlers that the bot is using */
  @NonNull
  private static final List<GuidoHandler> handlers =
      Lots.list(
          new DecorationsHandler(),
          Guido.languageHandler,
          new LinkHandler(),
          new MatchCalculator(),
          new MatchMakingHandler(),
          new PGMMatchHandler(),
          new QueueHandler(),
          new GuidoMessagesController(),
          new TestHandler(),
          Guido.dataLoader);

  /** The command manager of the bot */
  private static CommandManager commandManager;

  /** The server to receive implementations */
  @NonNull private static BotServer server = new GuidoFallbackServer();

  /**
   * The main method of the bot.
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
    HashMap<String, String> argsMaps = Maps.fromStringArray("=", args);
    Guido.timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            Guido.cache.run();
          }
        },
        0L,
        1000L);
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, exception) -> Guido.logger.log(Level.SEVERE, exception, null));
    JDA jda = Guido.setupJda(argsMaps);
    Guido.setupDataLoader(argsMaps);
    Guido.createServer(argsMaps);
    Guido.registerCommands(argsMaps, jda);
    Guido.languageHandler.load("en", "es", "fr");
    for (GuidoHandler handler : Guido.handlers) {
      handler.register(jda);
    }
  }

  /**
   * Register all the commands for the bot
   *
   * @param argsMaps the arguments to get the prefix
   * @param jda the instance of jda for the command manager
   */
  public static void registerCommands(HashMap<String, String> argsMaps, JDA jda) {
    ManagerOptions options = new ManagerOptions();
    options.setDeleteCommands(false);
    options.setDeleteErrors(false);
    options.setDeleteSuccess(false);
    options.setEmbedMessages(true);
    options.setSuccess(new Color(Integer.decode("#f48d0e")));
    options.setError(new Color(Integer.decode("#db150a")));
    Guido.commandManager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", argsMaps.getOrDefault("prefix", ".")),
            options,
            Guido.languageHandler,
            new GuidoProvidersRegistry(Guido.languageHandler),
            new GuidoPermissionChecker(Guido.languageHandler, Guido.dataLoader));
    for (Object cmd :
        Lots.list(
            new AdministrationCommands(),
            new CategoryCommands(),
            new ChannelCommands(),
            new DeveloperCommands(),
            new HelpCommand(),
            new LadderCommands(),
            new LangCommands(),
            new LeaderboardCommands(),
            new MatchCommands(),
            new QueueCommands(),
            new RangesCommand(),
            new TeamCommands(),
            new TestCommands(),
            new TokenCommands(),
            new UserCommands(),
            new VoiceChannelCommands())) {
      Guido.commandManager.registerCommand(cmd);
    }
  }

  /**
   * Creates the server and the receptors
   *
   * @param argsMaps the map to get the port and timeout of the server
   */
  public static void createServer(HashMap<String, String> argsMaps) {
    try {
      int port = Integer.parseInt(argsMaps.getOrDefault("port", "3000"));
      long timeout = Long.parseLong(argsMaps.getOrDefault("timeout", "3000"));
      Guido.server = new GuidoServer(port, timeout);
      Guido.server.start();
    } catch (IOException e) {
      Guido.logger.log(Level.SEVERE, e, () -> "Socket server could not be initialized");
    } catch (NumberFormatException e) {
      Guido.logger.log(Level.SEVERE, e, () -> "Port or timeout could not be parsed");
    }
  }

  /**
   * Setups the data loader for the server
   *
   * @param argsMaps the map to get the desired type of loader and the parameters of it
   */
  public static void setupDataLoader(HashMap<String, String> argsMaps) {
    if (argsMaps.get("loader") != null) {
      if (argsMaps.get("loader").equalsIgnoreCase("jsongo")) {
        try {
          DataLoader old = Guido.dataLoader;
          Guido.dataLoader =
              new JsongoDataLoader(
                  argsMaps.getOrDefault("uri", "none"),
                  argsMaps.getOrDefault("database", "testing-database"));
          Guido.languageHandler.setDataLoader(Guido.dataLoader);
          Guido.handlers.remove(old);
          Guido.handlers.add(Guido.dataLoader);
        } catch (Exception e) {
          Guido.logger.log(Level.SEVERE, e, () -> "Jsongo loader could not be initialized");
        }
      }
    }
  }

  /**
   * Creates the jda connection for the bot
   *
   * @param argsMaps the arguments to get the token
   * @return the instance of the jda setup
   */
  @NonNull
  public static JDA setupJda(HashMap<String, String> argsMaps) {
    JDA jda = Guido.connection.createConnection(argsMaps.getOrDefault("token", ""));
    jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing(".help .ayuda .?"));
    jda.setEventManager(new AnnotatedEventManager());
    return jda;
  }

  /** Stops the bot */
  public static void stop() {
    Guido.clearCache();
    Guido.closeServer();
    Guido.languageHandler.stop();
    for (GuidoHandler handler : Guido.handlers) {
      handler.unregister();
    }
    JDA jda = Guido.connection.getJda();
    if (jda != null) {
      jda.shutdown();
    }
    System.exit(0);
  }

  /** CLoses the bot server */
  public static void closeServer() {
    try {
      Guido.server.close();
    } catch (IOException e) {
      Guido.logger.log(Level.SEVERE, e, null);
    }
  }

  /** Clears all the cached items from the bot */
  public static void clearCache() {
    for (SoftReference<Catchable> reference : Guido.cache.copy()) {
      Catchable catchable = reference.get();
      if (catchable instanceof BotCatchable) {
        try {
          ((BotCatchable) catchable).unload(true);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
    Guido.cache.getMap().clear();
  }

  /**
   * Calls an event. This will get all the listeners for the event and call it for each of them
   *
   * @param event the event to be called
   */
  public static void call(@NonNull Event event) {
    Guido.listenerManager.call(event);
  }

  /**
   * Calls an event. As in {@link #call(Event)} but returns whether it was cancelled
   *
   * @param cancellable the event to be called
   * @return true if the event was cancelled
   * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
   */
  public static boolean call(@NonNull GuidoCancellable cancellable) {
    return Guido.listenerManager.call(cancellable);
  }

  /**
   * Get a handler using its class. This will loop and check if the class of the handler matches the
   * one queried
   *
   * @param clazz the class to match
   * @param <T> the type for guido handlers
   * @return the guido handler
   * @throws IllegalStateException if the handler was not found
   */
  public static <T extends GuidoHandler> T getHandler(@NonNull Class<T> clazz) {
    for (GuidoHandler handler : Guido.handlers) {
      if (handler.getClass() == clazz) {
        return clazz.cast(handler);
      }
    }
    throw new IllegalStateException("The handler with class " + clazz + " has not been added yet");
  }

  /**
   * Get the list of handlers that implement a class of guido handler
   *
   * @param tClass the class of guido handler that they must implement
   * @param <T> the type of the guido handler
   * @return the list of handlers
   */
  @NonNull
  public static <T extends GuidoHandler> List<T> getHandlers(@NonNull Class<T> tClass) {
    List<T> handlers = new ArrayList<>();
    for (GuidoHandler handler : Guido.handlers) {
      if (tClass.isAssignableFrom(handler.getClass())) {
        handlers.add(tClass.cast(handler));
      }
    }
    return handlers;
  }

  /**
   * Get the cache of the bot
   *
   * @return the cache
   */
  @NonNull
  public static MemoryCache getCache() {
    return Guido.cache;
  }

  /**
   * Get the listener manager that the bot is using
   *
   * @return the bot's listener manager
   */
  @NonNull
  public static ListenerManager getListenerManager() {
    return Guido.listenerManager;
  }

  /**
   * Get the data loader that guido is using
   *
   * @return the data loader that guido is using
   */
  @NonNull
  public static BotDataLoader getDataLoader() {
    return Guido.dataLoader;
  }

  /**
   * Get the bot jda connection
   *
   * @return the jda bot connection
   */
  @NonNull
  public static GuidoJdaConnection getConnection() {
    return Guido.connection;
  }

  /**
   * Get the language handler for guido
   *
   * @return the guido language handler
   */
  @NonNull
  public static GuidoLanguageHandler getLanguageHandler() {
    return Guido.languageHandler;
  }

  /**
   * Get the server which is used by the bot
   *
   * @return the server
   */
  @NonNull
  public static BotServer getServer() {
    return Guido.server;
  }

  /**
   * Get the command manager of the bot
   *
   * @return the command manager
   * @throws NullPointerException if the command manager was not initialized
   */
  @NonNull
  public static CommandManager getCommandManager() {
    return Validate.notNull(Guido.commandManager, "Command manager has not been initialized");
  }

  /**
   * Get the timer used in other instances of the bot
   *
   * @return the timer
   */
  @NonNull
  public static Timer getTimer() {
    return Guido.timer;
  }

  /**
   * Get the logger that the bot is using to print messages
   *
   * @return the logger
   */
  @NonNull
  public static Logger getLogger() {
    return Guido.logger;
  }
}
