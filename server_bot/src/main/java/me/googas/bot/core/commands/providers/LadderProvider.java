package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.util.Lang;

public class LadderProvider implements JdaArgumentProvider<Ladder> {

  @NonNull private final GuidoBotRuntime runtime;

    public LadderProvider(@NonNull GuidoBotRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
  public @NonNull Class<Ladder> getClazz() {
    return Ladder.class;
  }

  @NonNull
  @Override
  public Ladder fromString(@NonNull String string, @NonNull CommandContext context)
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
