package com.starfishst.bukkit.dependencies.pgm.commands.provider;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;

/** Provides parties in commands */
public class PartyProvider implements BukkitExtraArgumentProvider<Party> {

  @Override
  public @NonNull Class<Party> getClazz() {
    return Party.class;
  }

  @NonNull
  @Override
  public Party getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    BukkitLocaleFile locale = Guido.getLanguageHandler().getFile(context);
    if (context.getSender() instanceof Player) {
      MatchPlayer player = PGM.get().getMatchManager().getPlayer((Player) context.getSender());
      if (player != null) {
        return player.getParty();
      }
    }
    throw new ArgumentProviderException(locale.get("player-only"));
  }
}
