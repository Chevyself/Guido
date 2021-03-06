package me.googas.guido;

import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import lombok.NonNull;
import me.googas.commands.jda.CommandManager;
import me.googas.commands.jda.DefaultListenerOptions;
import me.googas.commands.jda.providers.registry.JdaProvidersRegistry;
import me.googas.guido.commands.EloCommands;
import me.googas.guido.commands.FunCommands;
import me.googas.guido.commands.GuidoCommands;
import me.googas.guido.commands.LinkCommands;
import me.googas.guido.commands.providers.ActivityTypeProvider;
import me.googas.guido.commands.providers.MinecraftLinkProvider;
import me.googas.guido.commands.providers.OnlineStatusProvider;
import me.googas.guido.config.GuidoConfig;
import me.googas.guido.db.sql.PropertiesSchemaSupplier;
import me.googas.guido.db.sql.SqlLinksSubloader;
import me.googas.guido.receptors.LinkReceptors;
import me.googas.guido.receptors.auth.AuthReceptors;
import me.googas.guido.receptors.auth.SingleTokenAuthentication;
import me.googas.lazy.Loader;
import me.googas.lazy.sql.LazySQL;
import me.googas.lazy.sql.LazySchema;
import me.googas.net.api.messages.Message;
import me.googas.net.cache.Cache;
import me.googas.net.cache.MemoryCache;
import me.googas.net.sockets.json.adapters.MessageDeserializer;
import me.googas.net.sockets.json.server.JsonSocketServer;
import me.googas.starbox.scheduler.Scheduler;
import me.googas.starbox.scheduler.TimerScheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

// TODO move getters to static class
public class GuidoBot {

  @NonNull @Getter private static final GuidoConfig config = GuidoConfig.load();
  @NonNull @Getter private static final Scheduler scheduler = new TimerScheduler();

  @NonNull @Getter
  private static final Cache cache = new MemoryCache().register(GuidoBot.scheduler);

  @NonNull
  private static final GuidoMessagesProvider messagesProvider = new GuidoMessagesProvider();

  private static JDA jda;
  private static Loader loader;
  private static JsonSocketServer server;
  private static CommandManager manager;

  public static void main(String[] args)
      throws LoginException, InterruptedException, IOException, SQLException,
          ClassNotFoundException {
    GuidoBot.jda =
        JDABuilder.create(GuidoBot.config.getToken(), Arrays.asList(GatewayIntent.values()))
            .build()
            .awaitReady();
    GuidoBot.setupLoader();
    GuidoBot.setupServer();
    GuidoBot.setupCommands();
  }

  public static void setupLoader() throws SQLException, ClassNotFoundException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    GuidoBot.loader =
        LazySQL.at(GuidoBot.config.getDatabase().getUrl())
            .add(new SqlLinksSubloader.Builder())
            .setSchema(
                new LazySchema(
                    LazySchema.Type.SQL,
                    PropertiesSchemaSupplier.of(GuidoFiles.Resources.Schemas.SQL)))
            .build()
            .start();
  }

  public static void setupServer() throws IOException {
    GuidoBot.server =
        JsonSocketServer.listen(GuidoBot.config.getServerPort())
            .addReceptors(new LinkReceptors(), new AuthReceptors(GuidoBot.config.getServerToken()))
            .auth(new SingleTokenAuthentication())
            .handle(Throwable::printStackTrace)
            .setGson(
                new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Message.class, new MessageDeserializer()))
            .start();
  }

  public static void setupCommands() {
    // Registry
    JdaProvidersRegistry registry = new JdaProvidersRegistry(GuidoBot.messagesProvider);
    registry.addProvider(new ActivityTypeProvider());
    registry.addProvider(new MinecraftLinkProvider());
    registry.addProvider(new OnlineStatusProvider());
    // Options
    DefaultListenerOptions listenerOptions = new DefaultListenerOptions();
    listenerOptions.setPrefix(GuidoBot.config.getPrefix());
    listenerOptions.setEmbedMessages(true);
    // Manager
    GuidoBot.manager =
        new CommandManager(
                registry,
                GuidoBot.messagesProvider,
                () -> GuidoBot.messagesProvider,
                GuidoBot.jda,
                listenerOptions)
            .parseAndRegisterAll(
                new EloCommands(), new FunCommands(), new GuidoCommands(), new LinkCommands());
  }

  @NonNull
  public static JDA getJda() {
    return Objects.requireNonNull(GuidoBot.jda, "JDA has not been initialized");
  }

  @NonNull
  public static Loader getLoader() {
    return Objects.requireNonNull(GuidoBot.loader, "Loader has not been initialized");
  }

  @NonNull
  public static JsonSocketServer getServer() {
    return Objects.requireNonNull(GuidoBot.server, "Server has not been initialized");
  }
}
