package com.starfishst.guido.pgm.api.events;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.starfishst.guido.pgm.api.Guido;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** A listener that also supports listening to packets */
public interface GuidoPacketListener extends GuidoListener, PacketListener {

  @Override
  default void register(@NotNull Plugin plugin) {
    GuidoListener.super.register(plugin);
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
  }

  @Override
  default boolean isEnabled() {
    return Guido.isProtocolLibConnected()
        && this.getSettings().getSettingOr("enabled", Boolean.class, false);
  }
}
