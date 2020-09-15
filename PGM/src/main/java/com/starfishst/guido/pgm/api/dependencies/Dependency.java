package com.starfishst.guido.pgm.api.dependencies;

import com.starfishst.guido.pgm.api.events.GuidoListener;
import java.util.Collection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** This object represents a dependency to which the plugin soft depends to */
public interface Dependency {

  /**
   * Get the listeners that the dependency can use
   *
   * @param plugin the plugin which is available to register them
   * @return the listeners
   */
  @NotNull
  Collection<GuidoListener> getListeners(@NotNull Plugin plugin);

  /**
   * Set whether the dependency is enabled
   *
   * @param bol the new value of enabled
   */
  void setEnabled(boolean bol);

  /**
   * Get the name of the dependency
   *
   * @return the name of the dependency
   */
  @NotNull
  String getName();

  /**
   * Whether the dependency is loaded in the class path
   *
   * @return true if the dependency is in the class path
   */
  boolean isEnabled();
}
