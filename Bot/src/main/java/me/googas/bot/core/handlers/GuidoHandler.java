package me.googas.bot.core.handlers;

import lombok.NonNull;
import me.googas.bot.Guido;
import net.dv8tion.jda.api.JDA;

/** A guido handler listens to JDA events */
public interface GuidoHandler {

  /**
   * Registers the guido handler events
   *
   * @param jda the instance of jda to register
   */
  default void register(@NonNull JDA jda) {
    jda.addEventListener(this);
    Guido.getListenerManager().registerListeners(this);
  }

  /** Closes the handler */
  void close();

  /** Unregisters a guido handler */
  default void unregister() {
    this.close();
  }
}
