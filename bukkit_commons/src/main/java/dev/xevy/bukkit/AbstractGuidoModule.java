package dev.xevy.bukkit;

import lombok.Getter;
import lombok.NonNull;

public abstract class AbstractGuidoModule implements GuidoModule {
  @NonNull @Getter protected final GuidoBukkitRuntime runtime;

  protected AbstractGuidoModule(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }
}
