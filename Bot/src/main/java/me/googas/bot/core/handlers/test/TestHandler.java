package me.googas.bot.core.handlers.test;

import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.bot.core.util.console.Console;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;

/** A handler for testing purposes */
public class TestHandler implements GuidoEventHandler {

  /**
   * Listen to guido events and print then
   *
   * @param event a guido event
   */
  @Listener
  public void onGuidoEvent(@NotNull GuidoEvent event) {
    Console.debug(event.getClass().getSimpleName() + " has been called: \n " + event.toString());
  }

  @Override
  public void close() {}
}
