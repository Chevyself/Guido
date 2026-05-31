package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Lang;

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
