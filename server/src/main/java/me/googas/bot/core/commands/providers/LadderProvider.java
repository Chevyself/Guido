package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.util.Lang;

public class LadderProvider implements JdaArgumentProvider<Ladder> {

  @Override
  public @NonNull Class<Ladder> getClazz() {
    return Ladder.class;
  }

  @NonNull
  @Override
  public Ladder fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      GuidoGuild guild =
          Guido.getHandlers()
              .getDiscordLoader()
              .getGuild(((GuildCommandContext) context).getGuild().getIdLong());
      Ladder ladder = guild.getLadder(s);
      if (ladder != null) {
        return ladder;
      } else {
        throw Lang.getException("invalid.ladder", Maps.singleton("string", s), context);
      }
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
