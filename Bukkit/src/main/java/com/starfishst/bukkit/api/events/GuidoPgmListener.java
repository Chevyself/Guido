package com.starfishst.bukkit.api.events;

import com.starfishst.bukkit.api.Guido;

/** A listener for pgm events and bukkit events */
public interface GuidoPgmListener extends GuidoListener {

  @Override
  default boolean isEnabled() {
    return Guido.isPgmConnected()
        && this.getSettings().getSettingOr("enabled", Boolean.class, false);
  }
}
