package com.starfishst.bukkit.api.dependencies;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.core.providers.type.IContextualProvider;
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
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @NotNull
  Collection<GuidoCommand> getCommands();

  /**
   * The argument providers required for the commands in this dependency
   *
   * @return the providers
   */
  Collection<IContextualProvider<?, CommandContext>> getProviders();

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
