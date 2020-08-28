package com.starfishst.guido;

import com.starfishst.commands.CommandManager;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.commands.PermissionChecker;
import com.starfishst.commands.providers.registry.ProvidersRegistryJDA;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.guido.commands.DeveloperCommands;
import com.starfishst.guido.commands.EloCommands;
import com.starfishst.guido.commands.TeamCommands;
import com.starfishst.guido.data.loader.GuidoFileLoader;
import com.starfishst.utils.events.Cancellable;
import com.starfishst.utils.events.Event;
import com.starfishst.utils.events.ListenerManager;
import java.util.HashMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.jetbrains.annotations.NotNull;

/** El bot para las rankeds de radiator springs */
// TODO separate the jda init method
public class Guido {

  /** The connection of guido with discord */
  @NotNull private static final GuidoJdaConnection connection = new GuidoJdaConnection();
  /** The listener manager for calling events */
  @NotNull private static final ListenerManager listenerManager = new ListenerManager();

  /** The data loader */
  @NotNull private static DataLoader dataLoader = new GuidoFileLoader();

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

    GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();
    PermissionChecker permissionChecker = () -> messagesProvider; // TODO
    CommandManager manager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", argsMaps.getOrDefault("prefix", "$")),
            new ManagerOptions(),
            messagesProvider,
            new ProvidersRegistryJDA(messagesProvider),
            permissionChecker);
    manager.registerCommand(new EloCommands());
    manager.registerCommand(new DeveloperCommands(jda));
    manager.registerCommand(new TeamCommands());

    listenerManager.registerListeners(dataLoader);
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
   * Get the data loader that guido is using
   *
   * @return the data loader that guido is using
   */
  @NotNull
  public static DataLoader getDataLoader() {
    return dataLoader;
  }
}
