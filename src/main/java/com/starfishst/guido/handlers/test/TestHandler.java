package com.starfishst.guido.handlers.test;

import com.starfishst.guido.api.events.GuidoEvent;
import com.starfishst.guido.handlers.GuidoEventHandler;
import com.starfishst.utils.events.Listener;
import org.jetbrains.annotations.NotNull;

/** A handler for testing purposes */
public class TestHandler implements GuidoEventHandler {

  @Listener
  public void onGuidoEvent(@NotNull GuidoEvent event) {
    System.out.println(event);
  }
}
