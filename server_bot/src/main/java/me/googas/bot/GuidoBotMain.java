package me.googas.bot;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.CoreFiles;
import me.googas.starbox.ProgramArguments;

@Slf4j
public class GuidoBotMain {

  private static class SingletonServerRuntime implements GuidoServerRuntime {

    @NonNull @Getter private final ProgramArguments arguments;
    @NonNull private final Path currentDirectory = Path.of(System.getProperty("user.dir"));

    private SingletonServerRuntime(@NonNull ProgramArguments arguments) {
      this.arguments = arguments;
    }

    public static SingletonServerRuntime of(@NonNull String[] args) {
      return new SingletonServerRuntime(ProgramArguments.construct(args));
    }

    @Override
    public @NonNull File currentDirectory() {
      return this.currentDirectory.toFile();
    }

    @Override
    public @NonNull InputStream getResource(@NonNull String name) {
      return CoreFiles.getResource(name);
    }
  }

  static class SingletonGuidoBotConfig implements GuidoBotConfig {

    @NonNull @Getter private final String mongoUri;
    @NonNull @Getter private final String database;
    @NonNull @Getter private final String discordToken;
    @Getter private final int serverPort;
    @Getter private final long timeout;
    @Getter private final long guildId;

    private SingletonGuidoBotConfig(
        @NonNull String mongoUri,
        @NonNull String database,
        @NonNull String discordToken,
        int serverPort,
        long timeout,
        long guildId) {
      this.mongoUri = mongoUri;
      this.database = database;
      this.discordToken = discordToken;
      this.serverPort = serverPort;
      this.timeout = timeout;
      this.guildId = guildId;
    }

    @NonNull
    public static SingletonGuidoBotConfig ofProperties(@NonNull Properties properties) {
      return new SingletonGuidoBotConfig(
          properties.getProperty("mongo_uri", "mongodb://localhost:27017"),
          properties.getProperty("database", "guido"),
          properties.getProperty("discord_token", "https://discord.com/developers/"),
          Integer.parseInt(properties.getProperty("server_port", "3366")),
          Long.parseLong(properties.getProperty("timeout", "10000")),
          Long.parseLong(properties.getProperty("guild_id", "1511402659767128291")));
    }

    @NonNull
    public static SingletonGuidoBotConfig load() throws IOException {
      File file = CoreFiles.getFileOrResource(BOT_PROPERTIES_FILE_NAME);
      Properties properties = new Properties();
      try (InputStream stream = new FileInputStream(file)) {
        properties.load(stream);
      }
      return SingletonGuidoBotConfig.ofProperties(properties);
    }
  }

  @NonNull private static final String BOT_PROPERTIES_FILE_NAME = "bot.properties";

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
   * <p>'uri' the connection uri for mongo.
   *
   * <p>'database' the database to use
   *
   * <p>'port' the port of the socket
   *
   * <p>'timeout' the time out for requests
   *
   * @param args the desired arguments for the bot
   */
  public static void main(@NonNull String[] args) throws IOException {
    SingletonGuidoBotConfig config = SingletonGuidoBotConfig.load();

    new GuidoBot(SingletonServerRuntime.of(args), config).start();
  }
}
