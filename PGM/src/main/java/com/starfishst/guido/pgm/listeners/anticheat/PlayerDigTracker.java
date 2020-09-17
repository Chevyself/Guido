package com.starfishst.guido.pgm.listeners.anticheat;

import com.comphenix.protocol.events.PacketAdapter;
import com.starfishst.guido.pgm.api.events.GuidoPacketListener;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

/** Tracks if a player is digging */
public class PlayerDigTracker extends PacketAdapter implements GuidoPacketListener {

  public PlayerDigTracker(@Nonnull AdapterParameteters params) {
    super(params);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return null;
  }
}
