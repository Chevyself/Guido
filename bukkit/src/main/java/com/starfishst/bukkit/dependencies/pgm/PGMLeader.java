package com.starfishst.bukkit.dependencies.pgm;

import java.lang.ref.SoftReference;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.team.TeamRole;

public class PGMLeader {

  @NonNull @Getter @Delegate private final SoftReference<MinecraftMatchTeamMember> captain;

  public PGMLeader(@NonNull MinecraftMatchTeamMember captain) {
    if (captain.getRole() != TeamRole.LEADER)
      throw new IllegalArgumentException(captain + " is not a team leader");
    this.captain = new SoftReference<>(captain);
  }

  @NonNull
  @Delegate
  public MinecraftMatchTeamMember validated() {
    return Objects.requireNonNull(this.captain.get(), "Reference to captain has expired");
  }
}
