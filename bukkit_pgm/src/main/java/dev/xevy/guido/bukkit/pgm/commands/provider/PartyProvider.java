package dev.xevy.guido.bukkit.pgm.commands.provider;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import dev.xevy.bukkit.BukkitLocaleFile;
import dev.xevy.bukkit.GuidoBukkitRuntime;
import lombok.NonNull;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;

/** Provides parties in commands */
public class PartyProvider implements BukkitExtraArgumentProvider<Party> {

  @NonNull private final GuidoBukkitRuntime runtime;

  public PartyProvider(@NonNull GuidoBukkitRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<Party> getClazz() {
    return Party.class;
  }

  @NonNull
  @Override
  public Party getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    BukkitLocaleFile locale = runtime.getLanguageHandler().getFile(context);
    if (context.getSender() instanceof Player) {
      MatchPlayer player = PGM.get().getMatchManager().getPlayer((Player) context.getSender());
      if (player != null) {
        return player.getParty();
      }
    }
    throw new ArgumentProviderException(locale.get("player-only"));
  }
}
