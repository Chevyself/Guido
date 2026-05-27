package me.googas.bot;

import com.github.chevyself.starbox.CommandManager;
import com.github.chevyself.starbox.jda.commands.JdaCommand;
import com.github.chevyself.starbox.jda.context.CommandContext;
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
import me.googas.bot.api.Guido;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.server.GuidoFallbackServer;
import me.googas.bot.core.server.GuidoServer;
import me.googas.net.cache.Catchable;
import me.googas.net.cache.MemoryCache;
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
    bot.getHandlerRegistry().setupDiscordLoader().setupLoader(arguments).register(jda);
    bot.setCommandManager(
        new GuidoCommandManager(jda, arguments, bot.getHandlerRegistry()).register());
    BotServer server = GuidoBot.createServer(arguments, bot);
    if (server != null) bot.setServer(server.registerHandlers(bot.getHandlerRegistry()));
    GuidoBot.log.info("Bot is ready to use");
  }

  /**
   * Creates the server and the receptors
   *
   * @param args the map to getId the port and timeout of the server
   */
  public static BotServer createServer(@NonNull ProgramArguments args, @NonNull GuidoBot bot) {
    try {
      int port = Integer.parseInt(args.getProperty("port", "3000"));
      long timeout = Long.parseLong(args.getProperty("timeout", "3000"));
      BotServer server = new GuidoServer(port, timeout, bot.getHandlerRegistry().getLoader());
      server.start();
      return server;
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
