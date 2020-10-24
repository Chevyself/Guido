package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.guido.api.data.discord.GuildData;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the data of a guild */
public class GuildDataProvider implements JdaExtraArgumentProvider<GuildData> {
  @Override
  public @NotNull Class<GuildData> getClazz() {
    return GuildData.class;
  }

  @Override
  public boolean provides(@NotNull Class<?> clazz) {
    return GuildData.class.isAssignableFrom(clazz);
  }

  @NotNull
  @Override
  public GuildData getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      return Guido.getDataLoader()
          .getGuildData(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
