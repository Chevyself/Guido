package me.googas.bot;

import com.starfishst.jda.CommandManager;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Timer;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;
import me.googas.bot.api.Guido;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.bot.core.server.GuidoServer;
import me.googas.commons.CoreFiles;
import me.googas.commons.ProgramArguments;
import me.googas.commons.cache.Catchable;
import me.googas.commons.cache.MemoryCache;
import me.googas.commons.events.ListenerManager;
import me.googas.commons.fallback.Fallback;
import me.googas.commons.fallback.SimpleFallback;
import me.googas.commons.log.LoggerFactory;
import me.googas.commons.log.formatters.CustomFormatter;
import me.googas.commons.scheduler.Scheduler;
import me.googas.commons.scheduler.TimerScheduler;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

/** The match making bot */
public class GuidoBot {

  @NonNull @Getter
  private static final Formatter formatter =
      new CustomFormatter(
          "[%level%] %day%/%month%/%year% [GuidoBot] %hour%:%minute%:%second%: %message% %stack%");

  @NonNull @Getter
  public static final Logger log =
      LoggerFactory.start("Guido", LoggerFactory.getConsoleHandler(GuidoBot.formatter));

  @NonNull @Getter private final MemoryCache cache = new MemoryCache();
  @NonNull @Getter private final Fallback fallback = new SimpleFallback();
  @NonNull @Getter private final GuidoJdaConnection connection = new GuidoJdaConnection();
  @NonNull @Getter private final ListenerManager listenerManager = new ListenerManager();
  @NonNull @Getter private final Scheduler scheduler = new TimerScheduler(new Timer());
  @NonNull @Getter private final GuidoHandlerRegistry handlerRegistry = new GuidoHandlerRegistry();
  // TODO what's up with this class with the new authenticator
  @NonNull @Getter @Setter private BotServer server = new GuidoFallbackServer();
  @Nullable @Setter @Getter private CommandManager commandManager;

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
    // Initialize the bot first
    GuidoBot bot = new GuidoBot();
    Guido.setInstance(bot);
    try {
      GuidoBot.log.addHandler(
          LoggerFactory.getFileHandler(
              GuidoBot.getFormatter(),
              CoreFiles.currentDirectory() + "/logs/",
              System.currentTimeMillis() + ".txt"));
    } catch (IOException ioException) {
      GuidoBot.log.info("File Handler for logger could not be added");
    }
    ProgramArguments arguments = ProgramArguments.construct(args);
    Thread.setDefaultUncaughtExceptionHandler(
        (thread, exception) -> GuidoBot.log.log(Level.SEVERE, exception, () -> ""));
    Time time = new Time(1, Unit.SECONDS);
    bot.getScheduler().repeat(time, time, bot.getCache());
    JDA jda = bot.getConnection().createConnection(arguments.getProperty("token", "none"));
    jda.setEventManager(new AnnotatedEventManager());
    bot.getHandlerRegistry().setupDiscordLoader().setupLoader(arguments).register(jda);
    bot.setCommandManager(
        new GuidoCommandManager(jda, arguments, bot.getHandlerRegistry()).register());
    BotServer server = GuidoBot.createServer(arguments, bot);
    if (server != null) bot.setServer(server);
    GuidoBot.log.info("Bot is ready to use");
  }

  /**
   * Creates the server and the receptors
   *
   * @param args the map to get the port and timeout of the server
   */
  @Nullable
  public static BotServer createServer(@NonNull ProgramArguments args, @NonNull GuidoBot bot) {
    try {
      int port = Integer.parseInt(args.getProperty("port", "3000"));
      long timeout = Long.parseLong(args.getProperty("timeout", "3000"));
      BotServer server = new GuidoServer(port, timeout, bot.getHandlerRegistry().getLoader());
      server.start();
    } catch (IOException | NumberFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

  @NonNull
  public GuidoBot clearCache() {
    for (SoftReference<Catchable> reference : this.cache.copy()) {
      Catchable catchable = reference.get();
      if (catchable instanceof BotCatchable) {
        try {
          ((BotCatchable) catchable).unload(true);
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
}
