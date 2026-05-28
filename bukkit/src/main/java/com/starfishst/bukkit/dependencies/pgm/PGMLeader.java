package com.starfishst.bukkit.dependencies.pgm;

import java.lang.ref.SoftReference;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;

public class PGMLeader {

  @NonNull @Getter @Delegate private final SoftReference<TeamMember> captain;

  public PGMLeader(@NonNull TeamMember captain) {
    if (captain.getRole() != TeamRole.LEADER)
      throw new IllegalArgumentException(captain + " is not a team leader");
    this.captain = new SoftReference<>(captain);
  }

  @NonNull
  @Delegate
  public TeamMember validated() {
    return Objects.requireNonNull(this.captain.get(), "Reference to captain has expired");
  }
}
