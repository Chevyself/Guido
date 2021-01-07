package me.googas.bot.core.handlers;

import lombok.NonNull;
import me.googas.bot.api.Guido;
import net.dv8tion.jda.api.JDA;

/** A guido handler listens to JDA events */
public interface GuidoHandler {

  /**
   * Registers the guido handler events
   *
   * @param jda the instance of jda to register
   */
  default GuidoHandler register(@NonNull JDA jda) {
    jda.addEventListener(this);
    Guido.getListenerManager().registerListeners(this);
    return this;
  }

  /** Closes the handler */
  default void onDisable() throws Throwable {}

  /** Unregisters a guido handler */
  default void unregister() {
    // TODO remove it from jda
    Guido.getListenerManager().unregister(this);
  }

  default void onEnable() {}
}
