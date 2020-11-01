package me.googas.bot.core.handlers;

import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.core.Guido;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** This listens to {@link GuidoEvent} and jda events */
public interface GuidoEventHandler extends GuidoHandler {

  @Override
  default void register(@NotNull JDA jda) {
    jda.addEventListener(this);
    Guido.getListenerManager().registerListeners(this);
  }

  @Override
  default void unregister() {
    GuidoHandler.super.unregister();
    Guido.getListenerManager().unregister(this);
  }
}
