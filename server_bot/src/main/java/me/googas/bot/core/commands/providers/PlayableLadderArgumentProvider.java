package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.matches.ladder.PlayableLadder;
import me.googas.bot.core.util.Lang;

public class PlayableLadderArgumentProvider implements JdaArgumentProvider<PlayableLadder> {

  @NonNull private final GuidoBotRuntime runtime;

    public PlayableLadderArgumentProvider(@NonNull GuidoBotRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
  public @NonNull Class<PlayableLadder> getClazz() {
    return PlayableLadder.class;
  }

  @NonNull
  @Override
  public PlayableLadder fromString(@NonNull String string, @NonNull CommandContext context)
      throws ArgumentProviderException {
    if (context instanceof GuildCommandContext guildContext) {
      return runtime
              .getLoader()
              .getGuidoGuildLoader()
              .getGuild(guildContext.getGuild())
              .getLadder(string)
              .orElseThrow(() -> Lang.getException("invalid.ladder", Maps.singleton("string", string), context));
    }
    String message = ((JdaMessagesProvider) context.getMessagesProvider()).guildOnly(context);
    throw new ArgumentProviderException(message);
  }
}
