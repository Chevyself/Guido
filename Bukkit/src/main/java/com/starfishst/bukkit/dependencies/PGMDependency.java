package com.starfishst.bukkit.dependencies;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.commands.PickCommands;
import com.starfishst.bukkit.commands.ReadyCommand;
import com.starfishst.bukkit.commands.providers.pgm.PartyProvider;
import com.starfishst.bukkit.commands.providers.pgm.PlayerInfoProvider;
import com.starfishst.bukkit.commands.providers.pgm.TeamMemberProvider;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.listeners.pgm.PGMStatsListener;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import com.starfishst.core.providers.type.IContextualProvider;
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
    return Lots.list(new PGMStatsListener(), new PGMMatchMakingListener());
  }

  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NotNull Collection<GuidoCommand> getCommands() {
    return Lots.list(new ReadyCommand(), new PickCommands());
  }

  /**
   * The argument providers required for the commands in this dependency
   *
   * @return the providers
   */
  @Override
  public Collection<IContextualProvider<?, CommandContext>> getProviders() {
    return Lots.list(new PartyProvider(), new PlayerInfoProvider(), new TeamMemberProvider());
  }
}
