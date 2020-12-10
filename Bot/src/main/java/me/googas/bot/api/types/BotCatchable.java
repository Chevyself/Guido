package me.googas.bot.api.types;

import java.util.logging.Level;
import lombok.NonNull;
import me.googas.bot.core.Guido;
import me.googas.commons.cache.Catchable;

/** An extension for catchable to use in the bot */
public interface BotCatchable extends Catchable {

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @NonNull
  default BotCatchable cache() {
    Guido.getCache().add(this);
    return this;
  }

  /**
   * Unloads this object from cache
   *
   * @param onRemove whether to call the method on remove
   */
  default void unload(boolean onRemove) {
    if (onRemove) {
      try {
        this.onRemove();
      } catch (Throwable throwable) {
        Guido.getLogger()
            .log(
                Level.SEVERE, throwable, () -> "There's been an error while unloading a catchable");
      }
    }
    Guido.getCache().remove(this);
  }
}
