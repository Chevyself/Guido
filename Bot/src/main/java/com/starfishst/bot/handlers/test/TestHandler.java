package com.starfishst.bot.handlers.test;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;

/** A handler for testing purposes */
public class TestHandler implements GuidoEventHandler {

  @Listener
  public void onGuidoEvent(@NotNull GuidoEvent event) {
    System.out.println(event);
  }

  @Override
  public void close() {}
}
