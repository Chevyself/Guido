package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.listeners.AntiCheatListener;
import com.starfishst.bukkit.listeners.anticheat.AutoClickDetector;
import com.starfishst.bukkit.listeners.anticheat.ReachDetector;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.ArrayList;
import java.util.Collection;
import me.googas.commons.Lots;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** The dependency in ProtocolLib */
public class ProtocolLibDependency implements Dependency {
  /** Whether pgm is loaded in the class path */
  private boolean enabled = false;

  @Override
  public @NotNull String getName() {
    return "ProtocolLib";
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
    return Lots.list(
        new AntiCheatListener(), new AutoClickDetector(plugin), new ReachDetector(plugin));
  }

  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NotNull Collection<GuidoCommand> getCommands() {
    return new ArrayList<>();
  }

  /**
   * The argument providers required for the commands in this dependency
   *
   * @return the providers
   */
  @Override
  public Collection<IContextualProvider<?, CommandContext>> getProviders() {
    return new ArrayList<>();
  }
}
