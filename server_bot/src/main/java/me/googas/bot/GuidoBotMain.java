package me.googas.bot;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import lombok.Getter;
import lombok.NonNull;
import me.googas.server.GuidoServerRuntime;
import me.googas.starbox.CoreFiles;
import me.googas.starbox.ProgramArguments;

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
  public static void main(@NonNull String[] args) {
    new GuidoBot(SingletonServerRuntime.of(args)).start();
  }
}
