package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.context.GuildCommandContext;
import com.starfishst.commands.jda.providers.type.JdaExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;

public class GuildDataProvider implements JdaExtraArgumentProvider<GuidoGuild> {
  @Override
  public @NonNull Class<GuidoGuild> getClazz() {
    return GuidoGuild.class;
  }

  @Override
  public boolean provides(@NonNull Class<?> clazz) {
    return GuidoGuild.class.isAssignableFrom(clazz);
  }

  @NonNull
  @Override
  public GuidoGuild getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    if (context instanceof GuildCommandContext) {
      return Guido.getHandlers()
          .getDiscordLoader()
          .getGuild(((GuildCommandContext) context).getGuild().getIdLong());
    }
    throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
  }
}
