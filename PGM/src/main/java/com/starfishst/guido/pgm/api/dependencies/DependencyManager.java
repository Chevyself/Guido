package com.starfishst.guido.pgm.api.dependencies;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

/** Manages dependencies. This provides them and manages the use of them */
public interface DependencyManager {

  /** Checks whether a dependency is enabled */
  void checkDependencies();

  /**
   * Check whether a dependency is enabled
   *
   * @param name the name of the dependency
   * @return true if the dependency is loaded in the classpath
   */
  boolean isEnabled(@NotNull String name);

  /**
   * Get a dependency by its name
   *
   * @param name the name of the dependency
   * @return the dependency
   */
  @NotNull
  Dependency getDependency(@NotNull String name);

  /**
   * Get the dependencies that this manager is handling
   *
   * @return the collection of dependencies
   */
  Collection<Dependency> getDependencies();
}
