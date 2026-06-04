package com.starfishst.bukkit.dependencies.pgm.listeners.matches;

import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;

public class PGMTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull @Getter private final Set<PGMTeamMember> members;
  @NonNull @Getter private final String name;

  public PGMTeam(int id, @NonNull Set<PGMTeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }
}
