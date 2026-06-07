package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.minecraft.MinecraftMatch;

public class MinecraftMatchEvent implements GuidoEvent {

  @NonNull @Getter private final MinecraftMatch match;

  public MinecraftMatchEvent(@NonNull MinecraftMatch match) {
    this.match = match;
  }
}
