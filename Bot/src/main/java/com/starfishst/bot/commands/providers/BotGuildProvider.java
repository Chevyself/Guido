package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the data of a guild */
public class BotGuildProvider implements JdaExtraArgumentProvider<BotGuild> {
  @Override
  public @NotNull Class<BotGuild> getClazz() {
    return BotGuild.class;
  }

  @NotNull
  @Override
  public BotGuild getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      return Guido.getDataLoader()
          .getGuildData(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
