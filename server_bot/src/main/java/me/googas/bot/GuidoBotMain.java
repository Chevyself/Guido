package me.googas.bot;

import java.io.File;
import java.nio.file.Path;
import lombok.Getter;
import lombok.NonNull;
import me.googas.server.GuidoRuntime;
import me.googas.starbox.ProgramArguments;

public class GuidoBotMain {

  private static class SingletonRuntime implements GuidoRuntime {

    @NonNull @Getter private final ProgramArguments arguments;
    @NonNull private final Path currentDirectory = Path.of(System.getProperty("user.dir"));

    private SingletonRuntime(@NonNull ProgramArguments arguments) {
      this.arguments = arguments;
    }

    public static SingletonRuntime of(@NonNull String[] args) {
      return new SingletonRuntime(ProgramArguments.construct(args));
    }

    @Override
    public @NonNull File currentDirectory() {
      return this.currentDirectory.toFile();
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
  public static void main(@NonNull String[] args) {
    new GuidoBot(SingletonRuntime.of(args)).start();
  }
}
