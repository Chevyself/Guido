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
    Guido.getListenerManager().parseAndRegister(this);
    return this;
  }

  /** Closes the handler */
  default void onDisable() throws Throwable {}

  /** Unregisters a guido handler */
  default void unregister() {
    JDA jda = Guido.getConnection().getJda();
    if (jda != null) jda.removeEventListener(this);
    Guido.getListenerManager().unregister(this);
  }

  default void onEnable() {}

  /**
   * Whether this handler has receptors to be registered in the server
   *
   * @return true if it has receptors by default it is false
   */
  default boolean hasReceptors() {
    return false;
  }
}
