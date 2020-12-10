package me.googas.bot.core;

import java.util.Scanner;
import java.util.logging.Level;
import javax.security.auth.login.LoginException;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

/** Setups connection with JDA */
public class GuidoJdaConnection {

  /** The jda instance used by the bot. */
  private JDA jda;

  /**
   * Get a token from the input of the console
   *
   * @param scanner to get the input from
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
      } catch (LoginException e) {
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
   * @throws LoginException if the discord token is wrong and authentication failed
   */
  public JDA connect(@NonNull String token) throws LoginException {
    JDA jda = JDABuilder.create(token, Lots.list(GatewayIntent.values())).build();
    long millis = 0;
    while (jda.getStatus() != JDA.Status.CONNECTED) {
      try {
        Thread.sleep(1);
        millis++;
      } catch (InterruptedException e) {
        Guido.getLogger()
            .log(
                Level.SEVERE, e, () -> "Thread was interrupted while trying to connect to discord");
      }
    }
    return jda;
  }

  /**
   * Get the connection with jda
   *
   * @return the connection with jda
   */
  public JDA getJda() {
    return this.jda;
  }

  /**
   * Validate the connection with jda to not be null
   *
   * @return the validated connection
   */
  @NonNull
  public JDA validatedJda() {
    return Validate.notNull(this.jda, "Bot is not connected with discord!");
  }
}
