package dev.xevy.guido.bukkit.pgm.listeners.matches;

import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;

@Deprecated
public class PGMTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull private final Set<PGMTeamMember> members;
  @NonNull @Getter private final String name;

  public PGMTeam(int id, @NonNull Set<PGMTeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  @Override
  public @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> getMembers() {
    return new ImmutableCollection<>(members);
  }

  @Override
  public @NonNull Optional<String> getPgmPartyId() {
    throw new UnsupportedOperationException();
  }
}
