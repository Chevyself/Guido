package com.starfishst.guido.pgm.listeners.anticheat;

import com.starfishst.guido.pgm.api.Guido;
import com.starfishst.guido.pgm.api.events.GuidoPacketListener;
import com.starfishst.guido.pgm.listeners.AntiCheatListener;

/** Detects cheats from players */
public interface AntiCheatDetector extends GuidoPacketListener {

  @Override
  default boolean isEnabled() {
    AntiCheatListener listener = Guido.getListener(AntiCheatListener.class);
    return listener != null && listener.isEnabled() && GuidoPacketListener.super.isEnabled();
  }
}
