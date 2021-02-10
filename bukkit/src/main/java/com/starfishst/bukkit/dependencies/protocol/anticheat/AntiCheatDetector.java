package com.starfishst.bukkit.dependencies.protocol.anticheat;

import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.protocol.PacketHandler;

/** Detects cheats from players */
public interface AntiCheatDetector extends PacketHandler {

  @Override
  default boolean isEnabled() {
    AntiCheatHandler listener = Guido.getModuleRegistry().get(AntiCheatHandler.class);
    return listener != null && listener.isEnabled() && PacketHandler.super.isEnabled();
  }
}
