package com.starfishst.guido.pgm.api.events;

import com.starfishst.guido.pgm.api.Guido;

/** A listener for pgm events and bukkit events */
public interface GuidoPgmListener extends GuidoListener {

  @Override
  default boolean isEnabled() {
    return Guido.isPgmConnected()
        && this.getSettings().getSettingOr("enabled", Boolean.class, false);
  }
}
