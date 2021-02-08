package com.starfishst.bukkit.modules;

import com.starfishst.bukkit.api.events.GuidoEvent;
import lombok.NonNull;
import me.googas.starbox.modules.Module;
import org.bukkit.event.EventHandler;

/** Listens to guido events */
public class TestHandler implements Module {

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
