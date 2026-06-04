package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.dependencies.pgm.PGMDependency;
import java.util.List;
import me.googas.starbox.compatibilities.CompatibilityManager;

public class GuidoCompatibilities extends CompatibilityManager {

  public GuidoCompatibilities() {
    super(List.of(new PGMDependency()));
  }
}
