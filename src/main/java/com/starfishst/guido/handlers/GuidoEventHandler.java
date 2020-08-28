package com.starfishst.guido.handlers;

import com.starfishst.guido.Guido;
import com.starfishst.guido.handlers.data.GuidoHandler;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/** This listens to {@link com.starfishst.utils.events.Event} */
public interface GuidoEventHandler extends GuidoHandler {

  @Override
  default void register(@NotNull JDA jda) {
    jda.addEventListener(this);
    Guido.getListenerManager().registerListeners(this);
  }
}
