package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;

public class MinecraftMatchStatusUpdatedEvent extends MinecraftMatchEvent {

  @NonNull @Getter private final MatchStatus status;

  public MinecraftMatchStatusUpdatedEvent(
      @NonNull MinecraftMatch match, @NonNull MatchStatus status) {
    super(match);
    this.status = status;
  }
}
