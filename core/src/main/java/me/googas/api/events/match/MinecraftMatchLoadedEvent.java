package me.googas.api.events.match;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.matches.minecraft.MinecraftMatch;

public class MinecraftMatchLoadedEvent implements GuidoEvent {

  @NonNull @Getter private final MinecraftMatch match;

  public MinecraftMatchLoadedEvent(@NonNull MinecraftMatch match) {
    this.match = match;
  }
}
