package com.starfishst.bukkit.dependencies.pgm.commands.provider;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingListener;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;

public class PGMHostedMatchProvider implements BukkitExtraArgumentProvider<PGMHostedMatch> {
    @Override
    public @NonNull Class<PGMHostedMatch> getClazz() {
        return PGMHostedMatch.class;
    }

    @Override
    public @NonNull PGMHostedMatch getObject(@NonNull CommandContext context) throws ArgumentProviderException {
        PGMMatchMakingListener listener = Guido.getListener(PGMMatchMakingListener.class);
        if (listener == null) throw new ArgumentProviderException("This command is not available");
        PGMHostedMatch match = listener.getMatch(context.getSender());
        if (match != null) return match;
        throw new ArgumentProviderException(
                Guido.getLanguageHandler().getFile(context).get("participant-only"));
    }
}
