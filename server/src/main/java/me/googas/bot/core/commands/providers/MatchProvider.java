package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

/** Provides a match by its id */
public class MatchProvider implements JdaArgumentProvider<AbstractMatch> {

  @Override
  public @NonNull Class<AbstractMatch> getClazz() {
    return AbstractMatch.class;
  }

  @NonNull
  @Override
  public AbstractMatch fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    AbstractMatch abstractMatch = Guido.getHandlers().getLoader().getMatches().getMatch(s);
    if (abstractMatch != null) {
      return abstractMatch;
    } else {
      throw Lang.getException("invalid.match", Maps.singleton("string", s), commandContext);
    }
  }
}
