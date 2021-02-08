package com.starfishst.bukkit.dependencies.protocol;

import com.starfishst.bukkit.dependencies.protocol.anticheat.AntiCheatHandler;
import com.starfishst.bukkit.dependencies.protocol.anticheat.AutoClickDetector;
import com.starfishst.bukkit.dependencies.protocol.anticheat.ReachDetector;
import com.starfishst.commands.bukkit.context.CommandContext;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.starbox.StarboxCommand;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.modules.Module;
import org.bukkit.plugin.Plugin;

/** The dependency in ProtocolLib */
public class ProtocolLibDependency implements Compatibility {
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
  public @NonNull Collection<Module> getModules(@NonNull Plugin plugin) {
    return Lots.list(
        new AntiCheatHandler(),
        new AutoClickDetector(plugin),
        new ReachDetector(plugin),
        new VelocityImprovement());
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
  }
  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NonNull Collection<StarboxCommand> getCommands() {
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
