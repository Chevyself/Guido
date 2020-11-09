package me.googas.bot.api.types;

import me.googas.bot.core.Guido;
import me.googas.bot.core.util.console.Console;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** An extension for catchable to use in the bot */
public interface BotCatchable extends Catchable {

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @NotNull
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
        Console.exception(throwable);
      }
    }
    Guido.getCache().remove(this);
  }
}
