package dev.xevy.guido.bukkit;

import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.guido.bukkit.pgm.PGMDependency;
import java.util.List;
import lombok.NonNull;
import me.googas.starbox.compatibilities.CompatibilityManager;

public class GuidoCompatibilities extends CompatibilityManager {

  public GuidoCompatibilities(@NonNull GuidoBukkitRuntime runtime) {
    super(List.of(new PGMDependency(runtime)));
  }
}
