package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.matches.Ladder;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.core.Guido;
import me.googas.commons.maps.Maps;

/** Provides ladders for command */
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
      BotGuild guild =
          Guido.getDataLoader()
              .getGuildDataOrCreate(((GuildCommandContext) context).getGuild().getIdLong());
      Ladder ladder = guild.getLadder(s);
      if (ladder != null) {
        return ladder;
      } else {
        throw new ArgumentProviderException(
            Guido.getLanguageHandler()
                .getFile(context)
                .get("invalid.ladder", Maps.singleton("string", s)));
      }
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
