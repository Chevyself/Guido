package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.api.events.GuidoListener;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/** Listens to guido events */
public class TestListener implements GuidoListener {

  /**
   * Listen to guido events and print them
   *
   * @param event the guido event
   */
  @EventHandler
  public void onGuidoEvent(GuidoEvent event) {
    System.out.println("Called " + event);
  }

  @Override
  public @NotNull String getName() {
    return "test";
  }

  @Override
  public void onUnload() {}
}
