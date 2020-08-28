package com.starfishst.guido;

import com.starfishst.commands.CommandManager;
import com.starfishst.commands.ManagerOptions;
import com.starfishst.commands.PermissionChecker;
import com.starfishst.commands.providers.registry.ProvidersRegistryJDA;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.guido.commands.DeveloperCommands;
import com.starfishst.guido.commands.EloCommands;
import com.starfishst.guido.commands.TeamCommands;
import com.starfishst.guido.data.loader.GuidoFileLoader;
import com.starfishst.utils.events.Cancellable;
import com.starfishst.utils.events.Event;
import com.starfishst.utils.events.ListenerManager;
import com.starfishst.utils.gson.GsonProvider;
import com.starfishst.utils.gson.adapters.jda.GuildAdapter;
import java.util.HashMap;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

/** El bot para las rankeds de radiator springs */
// TODO separate the jda init method
public class Guido {

  /**
   * The listener manager for calling events
   */
  @NotNull
  private static final ListenerManager listenerManager = new ListenerManager();

  /**
   * The data loader
   */
  @NotNull
  private static DataLoader dataLoader = new GuidoFileLoader();

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
    String token = argsMaps.getOrDefault("token", "");
    Scanner scanner = new Scanner(System.in);
    if (token.isEmpty()) {
      token = getTokenFromInput(scanner);
    }
    JDA jda = createConnection(token, scanner);
    jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.playing("A los pits"));
    jda.setEventManager(new AnnotatedEventManager());
    GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();
    PermissionChecker permissionChecker = () -> messagesProvider; // TODO
    CommandManager manager =
        new CommandManager(
            jda,
            argsMaps.getOrDefault("prefix", "$"),
            new ManagerOptions(),
            messagesProvider,
            new ProvidersRegistryJDA(messagesProvider), permissionChecker);
    manager.registerCommand(new EloCommands());
    manager.registerCommand(new DeveloperCommands(jda));
    manager.registerCommand(new TeamCommands());

    listenerManager.registerListeners(dataLoader);
  }

  /**
   * Try to connect to jda
   *
   * @param token the initial token
   * @param scanner to get a new token in case that the initial token is wrong
   * @return the jda api connection
   */
  @NotNull
  public static JDA createConnection(@NotNull String token, @NotNull Scanner scanner) {
    JDA jda = null;
    while (jda == null) {
      try {
        jda = connect(token);
      } catch (LoginException e) {
        System.out.println("Discord authentication failed");
        token = getTokenFromInput(scanner);
      }
    }
    return jda;
  }

  /**
   * Get a token from the input of the console
   *
   * @param scanner to get the input from
   * @return the token if an input was made
   */
  @NotNull
  public static String getTokenFromInput(@NotNull Scanner scanner) {
    System.out.println("Insert the bot token");
    while (true) {
      if (scanner.hasNext()) {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) {
          System.exit(0);
        } else {
          return input;
        }
        break;
      }
    }
    return "";
  }

  /**
   * Connects to discord
   *
   * @param token the discord bot token
   * @return the jda api
   * @throws LoginException if the discord token is wrong and authentication failed
   */
  public static JDA connect(@NotNull String token) throws LoginException {
    JDA jda = JDABuilder.create(token, Lots.list(GatewayIntent.values())).build();
    long millis = 0;
    while (jda.getStatus() != JDA.Status.CONNECTED) {
      try {
        System.out.println("Waiting for discord connection...");
        Thread.sleep(1);
        millis++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println(
        "Discord took " + Time.fromMillis(millis).toEffectiveString() + " to connect");
    return jda;
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
