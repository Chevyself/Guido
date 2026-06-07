package com.starfishst.bukkit.dependencies.pgm.listeners.matches;

import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.team.TeamRole;

public class PGMTeamMember implements MinecraftMatchTeamMember {
  @NonNull @Getter private final UUID id;
  @NonNull @Getter private final TeamRole role;

  public PGMTeamMember(@NonNull UUID id, @NonNull TeamRole role) {
    this.id = id;
    this.role = role;
  }
}
