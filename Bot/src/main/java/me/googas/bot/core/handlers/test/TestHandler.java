package me.googas.bot.core.handlers.test;

import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.commons.events.Listener;

/** A handler for testing purposes */
public class TestHandler implements GuidoEventHandler {

  /**
   * Listen to guido events and print then
   *
   * @param event a guido event
   */
  @Listener
  public void onGuidoEvent(@NonNull GuidoEvent event) {}

  @Override
  public void close() {}
}
