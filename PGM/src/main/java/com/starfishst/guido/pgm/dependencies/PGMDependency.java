package com.starfishst.guido.pgm.dependencies;

import com.starfishst.guido.pgm.api.dependencies.Dependency;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** The dependency to the PGM */
public class PGMDependency implements Dependency {

  /** Whether pgm is loaded in the class path */
  private boolean enabled = false;

  @Override
  public @NotNull String getName() {
    return "PGM";
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }

  @Override
  public @NotNull Collection<GuidoListener> getListeners(@NotNull Plugin plugin) {
    return new ArrayList<>();
  }
}
