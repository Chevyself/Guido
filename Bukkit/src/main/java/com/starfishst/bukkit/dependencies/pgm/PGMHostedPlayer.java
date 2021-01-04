package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.matches.HostedPlayer;
import java.lang.ref.SoftReference;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.commons.Validate;

public class PGMHostedPlayer {

  @NonNull @Getter @Delegate private final SoftReference<HostedPlayer> player;

  public PGMHostedPlayer(@NonNull HostedPlayer player) {
    this.player = new SoftReference<>(player);
  }

  @NonNull
  @Delegate
  public HostedPlayer validated() {
    return Validate.notNull(this.player.get(), "Reference to captain has expired");
  }
}
