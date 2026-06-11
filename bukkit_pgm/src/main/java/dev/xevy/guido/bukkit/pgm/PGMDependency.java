package dev.xevy.guido.bukkit.pgm;

import com.github.chevyself.starbox.bukkit.commands.BukkitCommand;
import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.parsers.CommandParser;
import com.github.chevyself.starbox.providers.StarboxContextualProvider;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.guido.bukkit.pgm.commands.PickCommands;
import dev.xevy.guido.bukkit.pgm.commands.ReadyCommand;
import dev.xevy.guido.bukkit.pgm.commands.provider.PGMHostedMatchProvider;
import dev.xevy.guido.bukkit.pgm.commands.provider.PGMHostedPlayerProvider;
import dev.xevy.guido.bukkit.pgm.commands.provider.PGMLeaderSenderProvider;
import dev.xevy.guido.bukkit.pgm.commands.provider.PartyProvider;
import dev.xevy.guido.bukkit.pgm.listeners.PGMStatsHandler;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import java.util.Collection;
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

  @NonNull private final GuidoBukkitRuntime runtime;

  public PGMDependency(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

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
        runtime.getCommandManager().getCommandParser();
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
        new PartyProvider(runtime),
        new PGMHostedMatchProvider(runtime),
        new PGMHostedPlayerProvider(runtime),
        new PGMLeaderSenderProvider(runtime));
  }

  @Override
  public @NonNull Collection<Module> getModules(@NonNull Plugin plugin) {
    return Lots.list(
        new PGMMatchMakingHandler(runtime),
        new PGMStatsHandler(runtime, runtime.getConfiguration().getContext()));
  }

  @Override
  public void onEnable() {
    JsonClient connection = runtime.getConnection().orElse(null);
    if (connection == null) return;
    runtime
        .getModuleRegistry()
        .get(PGMMatchMakingHandler.class)
        .ifPresent(handler -> handler.readyToHost(connection));
  }
}
