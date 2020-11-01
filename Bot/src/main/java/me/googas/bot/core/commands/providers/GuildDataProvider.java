package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.api.discord.GuildData;
import me.googas.bot.core.Guido;
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
          .getGuildDataOrCreate(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
