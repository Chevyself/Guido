package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.bot.Guido;
import me.googas.bot.api.types.match.BotMatch;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

/** Provides a match by its id */
public class MatchProvider implements JdaArgumentProvider<Match> {

  @Override
  public @NonNull Class<Match> getClazz() {
    return Match.class;
  }

  @NonNull
  @Override
  public Match fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    BotMatch match = Guido.getDataLoader().getMatch(s);
    if (match != null) {
      return match;
    } else {
      throw Lang.getException("invalid.match", Maps.singleton("string", s), commandContext);
    }
  }
}
