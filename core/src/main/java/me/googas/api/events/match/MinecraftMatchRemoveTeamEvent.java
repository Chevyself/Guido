package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;

public class MinecraftMatchRemoveTeamEvent implements GuidoEvent {

  @NonNull @Getter private final MinecraftMatch match;
  @NonNull @Getter private final MinecraftMatchTeam team;

  public MinecraftMatchRemoveTeamEvent(
      @NonNull MinecraftMatch match, @NonNull MinecraftMatchTeam team) {
    this.match = match;
    this.team = team;
  }
}
