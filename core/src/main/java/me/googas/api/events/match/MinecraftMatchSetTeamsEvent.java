package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.utility.ImmutableCollection;

public class MinecraftMatchSetTeamsEvent implements GuidoEvent {

  @NonNull @Getter private final MinecraftMatch match;
  @NonNull @Getter private final ImmutableCollection<? extends MinecraftMatchTeam> teams;

  public MinecraftMatchSetTeamsEvent(
      @NonNull MinecraftMatch match,
      @NonNull ImmutableCollection<? extends MinecraftMatchTeam> teams) {
    this.match = match;
    this.teams = teams;
  }
}
