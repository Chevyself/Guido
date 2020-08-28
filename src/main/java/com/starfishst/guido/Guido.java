package com.starfishst.guido;

import com.starfishst.commands.CommandManager;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.commands.providers.registry.ProvidersRegistryJDA;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.guido.commands.DeveloperCommands;
import com.starfishst.guido.commands.EloCommands;
import com.starfishst.guido.commands.LangCommands;
import com.starfishst.guido.commands.TeamCommands;
import com.starfishst.guido.handlers.data.GuidoHandler;
import com.starfishst.guido.handlers.data.loader.GuidoFileLoader;
import com.starfishst.guido.handlers.responsive.GuidoMessagesController;
import com.starfishst.guido.lang.GuidoLanguageHandler;
import com.starfishst.utils.events.Cancellable;
import com.starfishst.utils.events.Event;
import com.starfishst.utils.events.ListenerManager;
import java.util.HashMap;
import java.util.List;
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
  /** The list of handlers that the bot is using */
  @NotNull
  private static final List<GuidoHandler> handlers = Lots.list(new GuidoMessagesController());

  @NotNull private static DataLoader dataLoader = new GuidoFileLoader();

  /** The language handler for the bot */
  @NotNull
  private static GuidoLanguageHandler languageHandler = new GuidoLanguageHandler(dataLoader);

  /**
   * The main method of the bot.
   *
   * <p>Arguments include:
   *
   * <p>'token' for the token of the bot
   *
   * <p>'prefix' the prefix for the commands
   *
   * @param args the desired arguments for the bot
   */
  public static void main(String[] args) {
    HashMap<String, String> argsMaps = Maps.fromStringArray("=", args);

    JDA jda = connection.createConnection(argsMaps.getOrDefault("token", ""));
    jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("A los pits"));
    jda.setEventManager(new AnnotatedEventManager());

    CommandManager manager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", argsMaps.getOrDefault("prefix", "$")),
            new ManagerOptions(),
            languageHandler,
            new ProvidersRegistryJDA(languageHandler),
            new GuidoPermissionChecker(languageHandler, dataLoader));
    manager.registerCommand(new EloCommands());
    manager.registerCommand(new DeveloperCommands(jda));
    manager.registerCommand(new TeamCommands());
    manager.registerCommand(new LangCommands());
    languageHandler.load("en", "es", "fr");
    listenerManager.registerListeners(dataLoader);
    for (GuidoHandler handler : handlers) {
      handler.register(jda);
    }
  }

  /**
   * Calls an event. This will get all the listeners for the event and call it for each of them
   *
   * @param event the event to be called
   */
  public static void call(@NotNull Event event) {
    listenerManager.call(event);
  }

  /**
   * Calls an event. As in {@link #call(Event)} but returns whether it was cancelled
   *
   * @param cancellable the event to be called
   * @return true if the event was cancelled
   * @throws IllegalArgumentException cancellable is not an instance of {@link Event}
   */
  public static boolean call(@NotNull Cancellable cancellable) {
    return listenerManager.call(cancellable);
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
    for (GuidoHandler handler : handlers) {
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
    return listenerManager;
  }

  /**
   * Get the data loader that guido is using
   *
   * @return the data loader that guido is using
   */
  @NotNull
  public static DataLoader getDataLoader() {
    return dataLoader;
  }

  /**
   * Get the bot jda connection
   *
   * @return the jda bot connection
   */
  @NotNull
  public static GuidoJdaConnection getConnection() {
    return connection;
  }

  /**
   * Get the language handler for guido
   *
   * @return the guido language handler
   */
  @NotNull
  public static GuidoLanguageHandler getLanguageHandler() {
    return languageHandler;
  }
}
