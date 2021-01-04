package com.starfishst.bukkit.api.dependencies;

import com.starfishst.bukkit.GuidoPlugin;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.Collection;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

/** This object represents a dependency to which the plugin soft depends to */
public interface Dependency {

  /**
   * Get the listeners that the dependency can use
   *
   * @param plugin the plugin which is available to register them
   * @return the listeners
   */
  @NonNull
  Collection<Handler> getHandlers(@NonNull Plugin plugin);

  /** If the dependency is enabled this method will be called in {@link GuidoPlugin#onEnable()} */
  default void onEnable() {}

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
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @NonNull
  Collection<GuidoCommand> getCommands();

  /**
   * Whether the dependency is loaded in the class path
   *
   * @return true if the dependency is in the class path
   */
  boolean isEnabled();

  /**
   * Get the name of the dependency
   *
   * @return the name of the dependency
   */
  @NonNull
  String getName();
}
