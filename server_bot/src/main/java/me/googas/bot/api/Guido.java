package me.googas.bot.api;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoBot;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.GuidoJdaConnection;
import me.googas.net.api.Server;
import me.googas.net.sockets.json.server.JsonClientThread;
import me.googas.starbox.events.ListenerManager;
import me.googas.starbox.scheduler.Scheduler;

@Deprecated
public class Guido {

  @Getter private static GuidoBot instance;

  @NonNull
  public static GuidoBot validated() {
    return Objects.requireNonNull(Guido.instance, "Bot might not have been initialized");
  }

  @NonNull
  public static GuidoBot closeServer() {
    return Guido.validated().closeServer();
  }

  @Deprecated
  public static boolean stop() {
    try {
      Guido.validated().close();
      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void setInstance(@NonNull GuidoBot bot) {
    Guido.instance = bot;
  }

  public static @NonNull GuidoHandlerRegistry getHandlers() {
    return Guido.validated().getHandlers();
  }

  public static @NonNull GuidoJdaConnection getConnection() {
    return Guido.validated().getJdaConnection();
  }

  public static @NonNull ListenerManager getListenerManager() {
    return Guido.validated().getListeners();
  }

  public static @NonNull Scheduler getScheduler() {
    return Guido.validated().getScheduler();
  }

  public static @NonNull Server<JsonClientThread> getServer() {
    return Guido.validated().getServer();
  }

  @NonNull
  public static CommandManager<CommandContext, JdaCommand> getCommandManager() {
    return Objects.requireNonNull(
        Guido.validated().getCommandManager(), "Command manager might not have been initialized");
  }

  public static @NonNull Logger getLogger() {
    return GuidoBot.getLogger();
  }

  public static @NonNull GuidoAuthenticator getAuthenticator() {
    return Guido.validated().getAuthenticator();
  }
}
