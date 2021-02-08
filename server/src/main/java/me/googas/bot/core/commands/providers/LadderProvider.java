package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.context.GuildCommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

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
