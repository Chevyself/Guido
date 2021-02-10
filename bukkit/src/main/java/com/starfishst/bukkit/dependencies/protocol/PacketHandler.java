package com.starfishst.bukkit.dependencies.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.modules.GuidoModule;

/** A listener that also supports listening to packets */
public interface PacketHandler extends GuidoModule, PacketListener {

  @Override
  default void onEnable() {
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
    GuidoModule.super.onEnable();
  }

  @Override
  default boolean isEnabled() {
    return Guido.isProtocolLibConnected() && GuidoModule.super.isEnabled();
  }
}
