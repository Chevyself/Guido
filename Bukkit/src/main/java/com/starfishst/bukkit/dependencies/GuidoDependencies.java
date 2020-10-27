package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.dependencies.DependencyManager;
import java.util.Collection;
import java.util.Set;
import me.googas.commons.Lots;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** This class keeps track to which dependencies does guido soft depend to */
public class GuidoDependencies implements DependencyManager {

  /** The plugin to check if the dependencies is loaded */
  @NotNull private final Plugin plugin;

  /** The dependencies that guido has */
  @NotNull
  private final Set<Dependency> dependencies =
      Lots.set(new PGMDependency(), new ProtocolLibDependency());

  /**
   * Create the dependencies manager
   *
   * @param plugin the plugin that is handling this dependencies
   */
  public GuidoDependencies(@NotNull Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void checkDependencies() {
    for (Dependency dependency : this.dependencies) {
      if (this.plugin.getServer().getPluginManager().getPlugin(dependency.getName()) != null) {
        dependency.setEnabled(true);
        this.plugin.getLogger().info(dependency.getName() + " has been connected with Guido");
      }
    }
  }

  @Override
  public boolean isEnabled(@NotNull String name) {
    return this.getDependency(name).isEnabled();
  }

  @Override
  @NotNull
  public Dependency getDependency(@NotNull String name) {
    for (Dependency dependency : this.dependencies) {
      if (dependency.getName().equalsIgnoreCase(name)) {
        return dependency;
      }
    }
    throw new IllegalArgumentException(name + " is not a dependency");
  }

  @Override
  public Collection<Dependency> getDependencies() {
    return this.dependencies;
  }
}
