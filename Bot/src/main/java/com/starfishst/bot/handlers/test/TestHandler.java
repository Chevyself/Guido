package com.starfishst.bot.handlers.test;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.bot.util.console.Console;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;

/** A handler for testing purposes */
public class TestHandler implements GuidoEventHandler {

  /**
   * Listen to guido events and print then
   * @param event a guido event
   */
  @Listener
  public void onGuidoEvent(@NotNull GuidoEvent event) {
    Console.debug(event.toString());
  }

  @Override
  public void close() {}
}
