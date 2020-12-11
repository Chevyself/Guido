package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.dependencies.DependencyManager;
import com.starfishst.bukkit.dependencies.events.EventsDependency;
import java.util.Collection;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.Lots;
import org.bukkit.plugin.Plugin;

/** This class keeps track to which dependencies does guido soft depend to */
public class GuidoDependencies implements DependencyManager {

  /** The plugin to check if the dependencies is loaded */
  @NonNull private final Plugin plugin;

  /** The dependencies that guido has */
  @NonNull
  private final Set<Dependency> dependencies =
      Lots.set(new EventsDependency(), new PGMDependency(), new ProtocolLibDependency());

  /**
   * Create the dependencies manager
   *
   * @param plugin the plugin that is handling this dependencies
   */
  public GuidoDependencies(@NonNull Plugin plugin) {
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
  public boolean isEnabled(@NonNull String name) {
    return this.getDependency(name).isEnabled();
  }

  @Override
  @NonNull
  public Dependency getDependency(@NonNull String name) {
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
