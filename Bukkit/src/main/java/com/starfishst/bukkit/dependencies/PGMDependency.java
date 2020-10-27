package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.listeners.MatchMakingListener;
import com.starfishst.bukkit.listeners.StatsListener;
import java.util.Collection;
import me.googas.commons.Lots;
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
    return Lots.list(new StatsListener(), new MatchMakingListener());
  }
}
