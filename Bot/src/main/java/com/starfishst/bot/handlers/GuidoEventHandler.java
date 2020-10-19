package com.starfishst.bot.handlers;

import com.starfishst.bot.Guido;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** This listens to {@link com.starfishst.bot.api.events.GuidoEvent} and jda events */
public interface GuidoEventHandler extends GuidoHandler {

  @Override
  default void register(@NotNull JDA jda) {
    jda.addEventListener(this);
    Guido.getListenerManager().registerListeners(this);
  }

  @Override
  default void unregister() {
    Guido.getListenerManager().unregister(this);
  }
}
