package me.googas.bot.api.types;

import java.util.logging.Level;
import lombok.NonNull;
import me.googas.api.GuidoCatchable;
import me.googas.bot.Guido;

/** An extension for catchable to use in the bot */
public interface BotCatchable extends GuidoCatchable {

  @Override
  @NonNull
  default BotCatchable cache() {
    Guido.getCache().add(this);
    return this;
  }

  @Override
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
