package me.googas.bot.core.matches.queue;

import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;

public class ImmutableMinecraftMatchTeam implements MinecraftMatchTeam {

  @Getter private final int id;
  @NonNull @Getter private final Set<ImmutableMinecraftTeamMember> participants;
  @NonNull @Getter private final String name;

  public ImmutableMinecraftMatchTeam(
      int id, @NonNull Set<ImmutableMinecraftTeamMember> participants, @NonNull String name) {
    this.id = id;
    this.participants = participants;
    this.name = name;
  }

  @Override
  public @NonNull ImmutableCollection<? extends MinecraftMatchTeamMember> getMembers() {
    return new ImmutableCollection<>(participants);
  }
}
