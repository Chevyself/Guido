package com.starfishst.bukkit.dependencies.pgm.commands.provider;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import lombok.NonNull;

public class PGMHostedMatchProvider implements BukkitExtraArgumentProvider<PGMHostedMatch> {
  @Override
  public @NonNull Class<PGMHostedMatch> getClazz() {
    return PGMHostedMatch.class;
  }

  @Override
  public @NonNull PGMHostedMatch getObject(@NonNull CommandContext context)
      throws ArgumentProviderException {
    PGMMatchMakingHandler listener = Guido.getModuleRegistry().require(PGMMatchMakingHandler.class);
    PGMHostedMatch match = listener.getMatch(context.getSender());
    if (match != null) return match;
    throw new ArgumentProviderException(
        Guido.getLanguageHandler().getFile(context).get("participant-only"));
  }
}
