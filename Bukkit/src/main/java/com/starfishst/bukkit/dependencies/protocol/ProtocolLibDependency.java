package com.starfishst.bukkit.dependencies.protocol;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.dependencies.protocol.anticheat.AntiCheatHandler;
import com.starfishst.bukkit.dependencies.protocol.anticheat.AutoClickDetector;
import com.starfishst.bukkit.dependencies.protocol.anticheat.ReachDetector;
import com.starfishst.bukkit.dependencies.protocol.tab.TabListHandler;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import me.googas.commons.Lots;
import org.bukkit.plugin.Plugin;

/** The dependency in ProtocolLib */
public class ProtocolLibDependency implements Dependency {
  /** Whether pgm is loaded in the class path */
  private boolean enabled = false;

  @Override
  public @NonNull String getName() {
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
  public @NonNull Collection<Handler> getHandlers(@NonNull Plugin plugin) {
    return Lots.list(
        new AntiCheatHandler(),
        new AutoClickDetector(plugin),
        new ReachDetector(plugin),
        new TabListHandler(plugin),
        new VelocityImprovement());
  }

  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NonNull Collection<GuidoCommand> getCommands() {
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
