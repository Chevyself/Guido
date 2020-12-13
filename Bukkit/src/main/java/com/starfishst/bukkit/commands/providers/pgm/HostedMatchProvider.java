package com.starfishst.bukkit.commands.providers.pgm;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.HostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;

/** Provides commands with hosted matches */
public class HostedMatchProvider implements BukkitExtraArgumentProvider<HostedMatch> {
  @Override
  public @NonNull Class<HostedMatch> getClazz() {
    return HostedMatch.class;
  }

  @NonNull
  @Override
  public HostedMatch getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
    if (listener == null) throw new ArgumentProviderException("This command is not available");
    HostedMatch match = listener.getMatch(context.getSender());
    if (match != null) return match;
    throw new ArgumentProviderException(
        Guido.getLanguageHandler().getFile(context).get("participant-only"));
  }
}
