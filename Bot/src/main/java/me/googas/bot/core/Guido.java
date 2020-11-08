package me.googas.bot.core;

import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ManagerOptions;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import me.googas.api.loader.DataLoader;
import me.googas.bot.api.events.GuidoCancellable;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.server.BotServer;
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
import me.googas.bot.core.util.console.Console;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import me.googas.commons.cache.Catchable;
import me.googas.commons.cache.thread.Cache;
import me.googas.commons.cache.thread.ICatchable;
import me.googas.commons.events.Event;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The match making bot */
public class Guido {

  /** The timer which can be used in other instances of the bot */
  @NotNull private static final Timer timer = new Timer(Guido.class.getName());
  /** The connection of guido with discord */
  @NotNull private static final GuidoJdaConnection connection = new GuidoJdaConnection();
  /** The listener manager for calling events */
  @NotNull private static final ListenerManager listenerManager = new ListenerManager();

  /** The data loader for the bot */
  @NotNull private static BotDataLoader dataLoader = new GuidoFileLoader();

  /** The language handler for the bot */
  @NotNull
  private static final GuidoLanguageHandler languageHandler =
      new GuidoLanguageHandler(Guido.dataLoader);

  /** The list of handlers that the bot is using */
  @NotNull
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
  @Nullable private static CommandManager commandManager;

  /** The server to receive implementations */
  @NotNull private static BotServer server = new GuidoFallbackServer();

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
    if (argsMaps.getOrDefault("debug", "false").equalsIgnoreCase("true")) {
      Console.setDebug();
    }

    JDA jda = Guido.setupJda(argsMaps);

    Guido.setupDataLoader(argsMaps);
    Guido.createServer(argsMaps);
    Guido.registerCommands(argsMaps, jda);
    Console.info("Setting up language");
    Guido.languageHandler.load("en", "es", "fr");
    for (GuidoHandler handler : Guido.handlers) {
      Console.debug(handler.getClass().getSimpleName() + " has been registered as a handler");
      handler.register(jda);
    }
    Console.info("Guido is ready to use");
  }

  /**
   * Register all the commands for the bot
   *
   * @param argsMaps the arguments to get the prefix
   * @param jda the instance of jda for the command manager
   */
  public static void registerCommands(HashMap<String, String> argsMaps, JDA jda) {
    Console.debug("Starting to register commands");
    Guido.commandManager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", argsMaps.getOrDefault("prefix", ".")),
            new ManagerOptions(),
            Guido.languageHandler,
            new GuidoProvidersRegistry(Guido.languageHandler),
            new GuidoPermissionChecker(Guido.languageHandler, Guido.dataLoader));
    Console.info("Command manager is ready");
    for (Object cmd :
        Lots.list(
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
            new UserCommands())) {
      Console.debug("Registering commands in " + cmd.getClass().getSimpleName());
      Guido.commandManager.registerCommand(cmd);
    }
    Console.info("All commands have been registered");
  }

  /**
   * Creates the server and the receptors
   *
   * @param argsMaps the map to get the port and timeout of the server
   */
  public static void createServer(HashMap<String, String> argsMaps) {
    Console.debug("Creating bot's server");
    try {
      int port = Integer.parseInt(argsMaps.getOrDefault("port", "3000"));
      long timeout = Long.parseLong(argsMaps.getOrDefault("timeout", "3000"));
      Guido.server = new GuidoServer(port, timeout);
      Guido.server.start();
      Console.info(
          "Server has been setup in the port " + port + " using " + timeout + "ms of timeout");
    } catch (IOException e) {
      Console.exception(e, "Socket server could not be initialized");
    } catch (NumberFormatException e) {
      Console.exception(e, "Port or timeout could not be parsed");
    }
  }

  /**
   * Setups the data loader for the server
   *
   * @param argsMaps the map to get the desired type of loader and the parameters of it
   */
  public static void setupDataLoader(HashMap<String, String> argsMaps) {
    Console.info("Setting up data loader");
    if (argsMaps.get("loader") != null) {
      if (argsMaps.get("loader").equalsIgnoreCase("jsongo")) {
        Console.info("Attempting to register jsongo data loader");
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
          Console.exception(e, "Jsongo loader could not be initialized");
        }
        Console.debug("Jsongo has been setup");
      }
      Console.info("Using data loader: " + Guido.dataLoader.getClass().getSimpleName());
    }
  }

  /**
   * Creates the jda connection for the bot
   *
   * @param argsMaps the arguments to get the token
   * @return the instance of the jda setup
   */
  @NotNull
  public static JDA setupJda(HashMap<String, String> argsMaps) {
    Console.debug("Setting up jda");
    JDA jda = Guido.connection.createConnection(argsMaps.getOrDefault("token", ""));
    jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing(".help .ayuda .?"));
    jda.setEventManager(new AnnotatedEventManager());
    Console.debug("JDA has been setup");
    return jda;
  }

  /** Stops the bot */
  public static void stop() {
    Guido.clearCache();
    Guido.closeServer();
    Guido.languageHandler.stop();
    for (GuidoHandler handler : Guido.handlers) {
      Console.debug("Closing handler " + handler.getClass().getSimpleName());
      handler.unregister();
    }
    Console.info("Shutting down JDA");
    JDA jda = Guido.connection.getJda();
    if (jda != null) {
      jda.shutdown();
    }
    Console.info("Bot shutdown successful");
    System.exit(0);
  }

  /** CLoses the bot server */
  public static void closeServer() {
    Console.info("Closing server");
    try {
      Guido.server.close();
    } catch (IOException e) {
      Console.exception(e, "Server could not be closed properly");
    }
  }

  /** Clears all the cached items from the bot */
  public static void clearCache() {
    Console.info("Clearing cache...");
    for (SoftReference<Catchable> reference : Cache.copy()) {
      Catchable catchable = reference.get();
      if (catchable instanceof ICatchable) {
        Console.debug(catchable + " is being cleaned");
        try {
          ((ICatchable) catchable).unload(true);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    }
    Cache.getMap().clear();
  }

  /**
   * Calls an event. This will get all the listeners for the event and call it for each of them
   *
   * @param event the event to be called
   */
  public static void call(@NotNull Event event) {
    Guido.listenerManager.call(event);
  }

  /**
   * Calls an event. As in {@link #call(Event)} but returns whether it was cancelled
   *
   * @param cancellable the event to be called
   * @return true if the event was cancelled
   * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
   */
  public static boolean call(@NotNull GuidoCancellable cancellable) {
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
  public static <T extends GuidoHandler> T getHandler(@NotNull Class<T> clazz) {
    for (GuidoHandler handler : Guido.handlers) {
      if (handler.getClass() == clazz) {
        return clazz.cast(handler);
      }
    }
    throw new IllegalStateException("The handler with class " + clazz + " has not been added yet");
  }

  /**
   * Sets the data loader that the bot uses
   *
   * @param loader the new data loader
   */
  public static void setDataLoader(@NotNull BotDataLoader loader) {
    Guido.dataLoader = loader;
  }

  /**
   * Get the listener manager that the bot is using
   *
   * @return the bot's listener manager
   */
  @NotNull
  public static ListenerManager getListenerManager() {
    return Guido.listenerManager;
  }

  /**
   * Get the data loader that guido is using
   *
   * @return the data loader that guido is using
   */
  @NotNull
  public static BotDataLoader getDataLoader() {
    return Guido.dataLoader;
  }

  /**
   * Get the bot jda connection
   *
   * @return the jda bot connection
   */
  @NotNull
  public static GuidoJdaConnection getConnection() {
    return Guido.connection;
  }

  /**
   * Get the language handler for guido
   *
   * @return the guido language handler
   */
  @NotNull
  public static GuidoLanguageHandler getLanguageHandler() {
    return Guido.languageHandler;
  }

  /**
   * Get the server which is used by the bot
   *
   * @return the server
   */
  @NotNull
  public static BotServer getServer() {
    return Guido.server;
  }

  /**
   * Get the command manager of the bot
   *
   * @return the command manager
   * @throws NullPointerException if the command manager was not initialized
   */
  @NotNull
  public static CommandManager getCommandManager() {
    return Validate.notNull(Guido.commandManager, "Command manager has not been initialized");
  }

  /**
   * Get the timer used in other instances of the bot
   *
   * @return the timer
   */
  @NotNull
  public static Timer getTimer() {
    return Guido.timer;
  }
}
