package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;

/** A listener for pgm events and bukkit events */
public interface PGMHandler extends Handler {

  @Override
  default boolean isEnabled() {
    return Guido.isPPGMConnected() && this.getSettings().getOr("enabled", Boolean.class, false);
  }
}
