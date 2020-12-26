package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.context.GuildCommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.bot.Guido;
import me.googas.bot.api.types.discord.BotGuild;

public class GuildDataProvider implements JdaExtraArgumentProvider<BotGuild> {
  @Override
  public @NonNull Class<BotGuild> getClazz() {
    return BotGuild.class;
  }

  @Override
  public boolean provides(@NonNull Class<?> clazz) {
    return BotGuild.class.isAssignableFrom(clazz);
  }

  @NonNull
  @Override
  public BotGuild getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      return Guido.getDataLoader()
          .getGuildDataOrCreate(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
