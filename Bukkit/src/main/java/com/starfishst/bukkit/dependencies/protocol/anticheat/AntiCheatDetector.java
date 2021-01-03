package com.starfishst.bukkit.dependencies.protocol.anticheat;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.GuidoPacketListener;
import com.starfishst.bukkit.listeners.AntiCheatListener;

/** Detects cheats from players */
public interface AntiCheatDetector extends GuidoPacketListener {

  @Override
  default boolean isEnabled() {
    AntiCheatListener listener = Guido.getListener(AntiCheatListener.class);
    return listener != null && listener.isEnabled() && GuidoPacketListener.super.isEnabled();
  }
}
