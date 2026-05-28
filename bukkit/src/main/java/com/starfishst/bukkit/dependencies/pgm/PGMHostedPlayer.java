package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.matches.HostedPlayer;
import java.lang.ref.SoftReference;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;

public class PGMHostedPlayer {

  @NonNull @Getter @Delegate private final SoftReference<HostedPlayer> player;

  public PGMHostedPlayer(@NonNull HostedPlayer player) {
    this.player = new SoftReference<>(player);
  }

  @NonNull
  @Delegate
  public HostedPlayer validated() {
    return Objects.requireNonNull(this.player.get(), "Reference to captain has expired");
  }
}
