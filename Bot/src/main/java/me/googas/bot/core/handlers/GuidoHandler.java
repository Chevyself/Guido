package me.googas.bot.core.handlers;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** A guido handler listens to JDA events */
public interface GuidoHandler {

  /**
   * Registers the guido handler events
   *
   * @param jda the instance of jda to register
   */
  default void register(@NotNull JDA jda) {
    jda.addEventListener(this);
  }

  /** Closes the handler */
  void close();

  /** Unregisters a guido handler */
  default void unregister() {
    this.close();
  }
}
