package me.googas.bot.api;

import com.starfishst.commands.jda.CommandManager;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.bot.GuidoBot;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.GuidoJdaConnection;
import me.googas.bot.api.server.BotServer;
import me.googas.commons.Validate;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.scheduler.Scheduler;

public class Guido {

  @Nullable @Getter private static GuidoBot instance;

  @NonNull
  public static GuidoBot validated() {
    return Validate.notNull(Guido.instance, "Bot might not have been initialized");
  }

  @NonNull
  public static GuidoBot closeServer() {
    return Guido.validated().closeServer();
  }

  public static boolean stop() {
    return Guido.validated().stop();
  }

  public static void setInstance(@NonNull GuidoBot bot) {
    Guido.instance = bot;
  }

  public static @NonNull GuidoHandlerRegistry getHandlers() {
    return Guido.validated().getHandlerRegistry();
  }

  public static @NonNull MemoryCache getCache() {
    return Guido.validated().getCache();
  }

  public static @NonNull Fallback getFallback() {
    return Guido.validated().getFallback();
  }

  public static @NonNull GuidoJdaConnection getConnection() {
    return Guido.validated().getConnection();
  }

  public static @NonNull ListenerManager getListenerManager() {
    return Guido.validated().getListenerManager();
  }

  public static @NonNull Scheduler getScheduler() {
    return Guido.validated().getScheduler();
  }

  public static @NonNull BotServer getServer() {
    return Guido.validated().getServer();
  }

  @NonNull
  public static CommandManager getCommandManager() {
    return Validate.notNull(
        Guido.validated().getCommandManager(), "Command manager might not have been initialized");
  }

  public static @NonNull Logger getLogger() {
    return GuidoBot.getLog();
  }
}
