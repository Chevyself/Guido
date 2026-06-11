package dev.xevy.guido.bukkit.pgm.listeners.matches.creation;

import dev.xevy.bukkit.GuidoBukkitRuntime;
import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractTeamCreation implements TeamCreation {
  @NonNull @Getter protected final GuidoBukkitRuntime runtime;

  protected AbstractTeamCreation(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }
}
