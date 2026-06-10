package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.GuidoBukkitRuntime;
import com.starfishst.bukkit.dependencies.pgm.PGMDependency;
import java.util.List;
import lombok.NonNull;
import me.googas.starbox.compatibilities.CompatibilityManager;

public class GuidoCompatibilities extends CompatibilityManager {

  public GuidoCompatibilities(@NonNull GuidoBukkitRuntime runtime) {
    super(List.of(new PGMDependency(runtime)));
  }
}
