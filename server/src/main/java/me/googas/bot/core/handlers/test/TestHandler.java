package me.googas.bot.core.handlers.test;

import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.starbox.events.Listener;

/** A handler for testing purposes */
public class TestHandler implements GuidoHandler {

  /**
   * Listen to guido events and print then
   *
   * @param event a guido event
   */
  @Listener
  public void onGuidoEvent(@NonNull GuidoEvent event) {}

  @Override
  public void onDisable() {}
}
