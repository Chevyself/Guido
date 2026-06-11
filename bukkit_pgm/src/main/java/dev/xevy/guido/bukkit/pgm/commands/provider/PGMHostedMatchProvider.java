package dev.xevy.guido.bukkit.pgm.commands.provider;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import lombok.NonNull;

public class PGMHostedMatchProvider implements BukkitExtraArgumentProvider<PGMHostedMatch> {

  @NonNull private final GuidoBukkitRuntime runtime;

  public PGMHostedMatchProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<PGMHostedMatch> getClazz() {
    return PGMHostedMatch.class;
  }

  @Override
  public @NonNull PGMHostedMatch getObject(@NonNull CommandContext context)
      throws ArgumentProviderException {
    PGMMatchMakingHandler listener =
        runtime.getModuleRegistry().require(PGMMatchMakingHandler.class);
    PGMHostedMatch match = listener.getMatch(context.getSender());
    if (match != null) return match;
    throw new ArgumentProviderException(
        runtime.getLanguageHandler().getFile(context).get("participant-only"));
  }
}
