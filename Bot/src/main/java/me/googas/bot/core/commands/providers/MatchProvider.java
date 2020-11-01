package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Match;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.core.Guido;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** Provides a match by its id */
public class MatchProvider implements JdaArgumentProvider<Match> {

  @Override
  public @NotNull Class<Match> getClazz() {
    return Match.class;
  }

  @NotNull
  @Override
  public Match fromString(@NotNull String s, @NotNull CommandContext commandContext)
      throws ArgumentProviderException {
    LocaleFile locale = Guido.getLanguageHandler().getFile(commandContext);
    BotMatch match = Guido.getDataLoader().getMatch(s);
    if (match != null) {
      return match;
    } else {
      throw new ArgumentProviderException(locale.get("invalid.match", Maps.singleton("string", s)));
    }
  }
}
