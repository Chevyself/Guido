package com.starfishst.bukkit.dependencies.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketListener;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.events.Handler;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

/** A listener that also supports listening to packets */
public interface PacketHandler extends Handler, PacketListener {

  @Override
  default PacketHandler register(@NonNull Plugin plugin) {
    Handler.super.register(plugin);
    ProtocolLibrary.getProtocolManager().addPacketListener(this);
    return this;
  }

  @Override
  default boolean isEnabled() {
    return Guido.isProtocolLibConnected()
        && this.getSettings().getOr("enabled", Boolean.class, false);
  }
}
