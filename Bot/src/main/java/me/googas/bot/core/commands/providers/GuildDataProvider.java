package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.bot.core.Guido;

/** Provides the data of a guild */
public class GuildDataProvider implements JdaExtraArgumentProvider<GuildData> {
  @Override
  public @NonNull Class<GuildData> getClazz() {
    return GuildData.class;
  }

  @Override
  public boolean provides(@NonNull Class<?> clazz) {
    return GuildData.class.isAssignableFrom(clazz);
  }

  @NonNull
  @Override
  public GuildData getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      return Guido.getDataLoader()
          .getGuildDataOrCreate(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
