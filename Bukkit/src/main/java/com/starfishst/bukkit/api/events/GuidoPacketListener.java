package com.starfishst.bukkit.api.events;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.starfishst.bukkit.api.Guido;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

/** A listener that also supports listening to packets */
public interface GuidoPacketListener extends GuidoListener, PacketListener {

  @Override
  default void register(@NonNull Plugin plugin) {
    GuidoListener.super.register(plugin);
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
  }

  @Override
  default boolean isEnabled() {
    return Guido.isProtocolLibConnected()
        && this.getSettings().getOr("enabled", Boolean.class, false);
  }
}
