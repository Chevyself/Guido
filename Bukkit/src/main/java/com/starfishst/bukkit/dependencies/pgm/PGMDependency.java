package com.starfishst.bukkit.dependencies.pgm;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.Handler;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.dependencies.pgm.commands.PickCommands;
import com.starfishst.bukkit.dependencies.pgm.commands.ReadyCommand;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMHostedMatchProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMHostedPlayerProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMLeaderSenderProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PartyProvider;
import com.starfishst.bukkit.dependencies.pgm.listeners.PGMStatsHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.Collection;
import lombok.NonNull;
import me.googas.commons.Lots;
import me.googas.messaging.json.client.JsonClient;
import org.bukkit.plugin.Plugin;

/** The dependency to the PGM */
public class PGMDependency implements Dependency {

  /** Whether pgm is loaded in the class path */
  private boolean enabled = false;

  @Override
  public @NonNull String getName() {
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
  public @NonNull Collection<Handler> getHandlers(@NonNull Plugin plugin) {
    return Lots.list(new PGMStatsHandler(), new PGMMatchMakingHandler());
  }

  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NonNull Collection<GuidoCommand> getCommands() {
    return Lots.list(new ReadyCommand(), new PickCommands());
  }

  /**
   * The argument providers required for the commands in this dependency
   *
   * @return the providers
   */
  @Override
  public Collection<IContextualProvider<?, CommandContext>> getProviders() {
    return Lots.list(
        new PartyProvider(),
        new PGMHostedMatchProvider(),
        new PGMHostedPlayerProvider(),
        new PGMLeaderSenderProvider());
  }

  @Override
  public void onEnable() {
    PGMMatchMakingHandler listener =
        Guido.getHandlerRegistry().getHandler(PGMMatchMakingHandler.class);
    JsonClient connection = Guido.getClient().getConnection();
    if (listener == null || !listener.isEnabled() || connection == null) return;
    listener.readyToHost(connection);
  }
}
