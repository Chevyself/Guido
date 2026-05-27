package com.starfishst.bukkit.dependencies.pgm;

import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.parsers.CommandParser;
import com.github.chevyself.starbox.providers.StarboxContextualProvider;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.pgm.commands.PickCommands;
import com.starfishst.bukkit.dependencies.pgm.commands.ReadyCommand;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMHostedMatchProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMHostedPlayerProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PGMLeaderSenderProvider;
import com.starfishst.bukkit.dependencies.pgm.commands.provider.PartyProvider;
import com.starfishst.bukkit.dependencies.pgm.listeners.PGMStatsHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.groups.PGMGroupsHandler;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.api.utility.Lots;
import me.googas.net.sockets.json.client.JsonClient;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.modules.Module;
import org.bukkit.plugin.Plugin;

/** The dependency to the PGM */
public class PGMDependency implements Compatibility {

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

  /**
   * Get the commands to register with this dependency
   *
   * @return the collection of commands to register
   */
  @Override
  public @NonNull Collection<BukkitCommand> getCommands() {
    // return Lots.list(new ReadyCommand(), new PickCommands());
    CommandParser<CommandContext, BukkitCommand> commandParser =
        Guido.getPlugin().getCommandManager().getCommandParser();
    return Lots.list(new ReadyCommand(), new PickCommands()).stream()
        .map(commandParser::parseAllCommandsFrom)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * The argument providers required for the commands in this dependency
   *
   * @return the providers
   */
  @Override
  public Collection<StarboxContextualProvider<?, CommandContext>> getProviders() {
    return Lots.list(
        new PartyProvider(),
        new PGMHostedMatchProvider(),
        new PGMHostedPlayerProvider(),
        new PGMLeaderSenderProvider());
  }

  @Override
  public @NonNull Collection<Module> getModules(@NonNull Plugin plugin) {
    return Lots.list(new PGMGroupsHandler(), new PGMMatchMakingHandler(), new PGMStatsHandler());
  }

  @Override
  public void onEnable() {
    Optional<PGMMatchMakingHandler> optional =
        Guido.getModuleRegistry().get(PGMMatchMakingHandler.class);
    JsonClient connection = Guido.getClient().getConnection();
    if (optional.isEmpty() || !optional.get().isEnabled() || connection == null) return;
    optional.get().readyToHost(connection);
  }
}
