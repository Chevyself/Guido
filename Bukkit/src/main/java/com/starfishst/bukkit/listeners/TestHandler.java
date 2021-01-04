package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.api.events.Handler;
import lombok.NonNull;
import org.bukkit.event.EventHandler;

/** Listens to guido events */
public class TestHandler implements Handler {

  /**
   * Listen to guido events and print them
   *
   * @param event the guido event
   */
  @EventHandler
  public void onGuidoEvent(GuidoEvent event) {}

  @Override
  public @NonNull String getName() {
    return "test";
  }

  @Override
  public void onDisable() {}
}
