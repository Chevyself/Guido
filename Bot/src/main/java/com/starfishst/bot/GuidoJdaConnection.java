package com.starfishst.bot;

import com.starfishst.bot.util.console.Console;
import java.util.Scanner;
import javax.security.auth.login.LoginException;
import me.googas.commons.Lots;
import me.googas.commons.Validate;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Setups connection with JDA */
public class GuidoJdaConnection {

  /** The jda instance used by the bot. */
  @Nullable private JDA jda;

  /**
   * Get a token from the input of the console
   *
   * @param scanner to get the input from
   * @return the token if an input was made
   */
  @NotNull
  public static String getTokenFromInput(@NotNull Scanner scanner) {
    Console.info("Insert the bot token");
    while (true) {
      if (scanner.hasNext()) {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) {
          Console.info("Received signal to stop the bot");
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
  @NotNull
  public JDA createConnection(@NotNull String token) {
    Console.debug("Starting discord connection");
    this.jda = null;
    while (this.jda == null) {
      try {
        this.jda = this.connect(token);
      } catch (LoginException e) {
        Console.exception("Discord authentication failed");
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
  public JDA connect(@NotNull String token) throws LoginException {
    JDA jda = JDABuilder.create(token, Lots.list(GatewayIntent.values())).build();
    long millis = 0;
    Console.info("Waiting for connection");
    while (jda.getStatus() != JDA.Status.CONNECTED) {
      try {
        Thread.sleep(1);
        millis++;
      } catch (InterruptedException e) {
        Console.exception(e, "Thread was interrupted while trying to connect to discord");
      }
    }
    Console.info("Discord took " + Time.fromMillis(millis).toEffectiveString() + " to connect");
    return jda;
  }

  /**
   * Get the connection with jda
   *
   * @return the connection with jda
   */
  @Nullable
  public JDA getJda() {
    return this.jda;
  }

  /**
   * Validate the connection with jda to not be null
   *
   * @return the validated connection
   */
  @NotNull
  public JDA validatedJda() {
    return Validate.notNull(this.jda, "Bot is not connected with discord!");
  }
}
