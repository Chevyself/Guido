package me.googas.bot;

import java.io.Closeable;
import java.util.Objects;
import java.util.Scanner;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/** Setups connection with JDA */
@CustomLog
public class GuidoJdaConnection implements Closeable {

  /** The jda instance used by the bot. */
  private JDA jda;

  /**
   * Get a token from the input of the console
   *
   * @param scanner to getId the input from
   * @return the token if an input was made
   */
  @NonNull
  public static String getTokenFromInput(@NonNull Scanner scanner) {
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
   * Try to connect to jda. This method will keep trying until it gets tired
   *
   * @param token the initial token
   * @return the jda api connection
   */
  @NonNull
  public JDA createConnection(@NonNull String token) {
    this.jda = null;
    while (this.jda == null) {
      try {
        this.jda = this.connect(token);
      } catch (InterruptedException e) {
        token = GuidoJdaConnection.getTokenFromInput(new Scanner(System.in));
      }
    }
    return this.jda;
  }

  /**
   * Connects to discord
   *
   * @param token the discord bot token
   * @return the jda api
   */
  public JDA connect(@NonNull String token) throws InterruptedException {
    return JDABuilder.create(token, Lots.list(GatewayIntent.values())).build().awaitReady();
  }

  /**
   * Get the connection with jda
   *
   * @return the connection with jda
   */
  @NonNull
  public JDA getJda() {
    return Objects.requireNonNull(this.jda, "JDA has not been set yet");
  }

  @Override
  public void close() {
    if (this.jda != null) this.jda.shutdownNow();
  }
}
