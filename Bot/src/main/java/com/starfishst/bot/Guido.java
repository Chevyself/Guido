package com.starfishst.bot;

import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.commands.DeveloperCommands;
import com.starfishst.bot.commands.EloCommands;
import com.starfishst.bot.commands.LangCommands;
import com.starfishst.bot.commands.TeamCommands;
import com.starfishst.bot.commands.TokenCommands;
import com.starfishst.bot.commands.UserCommands;
import com.starfishst.bot.commands.providers.AuthLevelProvider;
import com.starfishst.bot.commands.providers.BotUserSenderProvider;
import com.starfishst.bot.handlers.data.GuidoHandler;
import com.starfishst.bot.handlers.data.loader.GuidoFileLoader;
import com.starfishst.bot.handlers.data.loader.MongoDataLoader;
import com.starfishst.bot.handlers.lang.GuidoLanguageHandler;
import com.starfishst.bot.handlers.responsive.GuidoMessagesController;
import com.starfishst.bot.server.GuidoFallbackServer;
import com.starfishst.bot.server.GuidoServer;
import com.starfishst.bot.util.console.Console;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ManagerOptions;
import com.starfishst.jda.providers.registry.JdaProvidersRegistry;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.Lots;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.events.Cancellable;
import me.googas.commons.events.Event;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.jetbrains.annotations.NotNull;

/** El bot para las rankeds de radiator springs */
public class Guido {

  /** The connection of guido with discord */
  @NotNull private static final GuidoJdaConnection connection = new GuidoJdaConnection();
  /** The listener manager for calling events */
  @NotNull private static final ListenerManager listenerManager = new ListenerManager();

  /** The data loader for the bot */
  @NotNull private static BotDataLoader dataLoader = new GuidoFileLoader();

  /** The list of handlers that the bot is using */
  @NotNull
  private static final List<GuidoHandler> handlers =
      Lots.list(new GuidoMessagesController(), Guido.dataLoader);

  /** The language handler for the bot */
  @NotNull
  private static GuidoLanguageHandler languageHandler = new GuidoLanguageHandler(Guido.dataLoader);

  /** The server to receive implementations */
  @NotNull private static Server server = new GuidoFallbackServer();

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

    JDA jda = Guido.connection.createConnection(argsMaps.getOrDefault("token", ""));
    jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("A los pits"));
    jda.setEventManager(new AnnotatedEventManager());

    if (argsMaps.get("loader") != null) {
      if (argsMaps.get("loader").equalsIgnoreCase("mongo")) {
        try {
          DataLoader old = Guido.dataLoader;
          Guido.dataLoader =
              new MongoDataLoader(
                  argsMaps.getOrDefault("uri", "none"),
                  argsMaps.getOrDefault("database", "testing-database"));
          Guido.languageHandler.setDataLoader(Guido.dataLoader);
          Guido.handlers.remove(old);
          Guido.handlers.add(Guido.dataLoader);
        } catch (Exception e) {
          Console.exception(e, "Mongo loader could not be initialized");
        }
      }
      Console.info("Using data loader: " + Guido.dataLoader.getClass());
    }
    try {
      Guido.server =
          new GuidoServer(
              Integer.parseInt(argsMaps.getOrDefault("port", "3000")),
              Long.parseLong(argsMaps.getOrDefault("timeout", "3000")));
      Guido.server.start();
    } catch (IOException e) {
      Console.exception(e, "Socket server could not be initialized");
    }
    JdaProvidersRegistry registry = new JdaProvidersRegistry(Guido.languageHandler);
    registry.addProvider(new AuthLevelProvider());
    registry.addProvider(new BotUserSenderProvider());
    CommandManager manager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", argsMaps.getOrDefault("prefix", "$")),
            new ManagerOptions(),
                Guido.languageHandler,
            registry,
            new GuidoPermissionChecker(Guido.languageHandler, Guido.dataLoader));
    manager.registerCommand(new DeveloperCommands(jda));
    manager.registerCommand(new EloCommands());
    manager.registerCommand(new LangCommands());
    manager.registerCommand(new TeamCommands());
    manager.registerCommand(new TokenCommands());
    manager.registerCommand(new UserCommands());
    Guido.languageHandler.load("en", "es", "fr");
    for (GuidoHandler handler : Guido.handlers) {
      handler.register(jda);
    }
  }

  /** Stops the bot */
  public static void stop() {
    List<ICatchable> copy = Cache.copy();
    for (ICatchable catchable : copy) {
      catchable.onRemove();
      catchable.unload();
    }
    try {
      Guido.server.close();
    } catch (IOException e) {
      Console.exception(e, "Server could not be closed properly");
    }
    Guido.languageHandler.stop();
    for (GuidoHandler handler : Guido.handlers) {
      handler.close();
      handler.unregister();
    }
    JDA jda = Guido.connection.getJda();
    if (jda != null) {
      jda.shutdown();
    }
    System.exit(0);
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
  public static boolean call(@NotNull Cancellable cancellable) {
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
  public static Server getServer() {
    return Guido.server;
  }
}
