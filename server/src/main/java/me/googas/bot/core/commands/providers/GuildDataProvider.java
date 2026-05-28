package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import com.github.chevyself.starbox.jda.providers.type.JdaExtraArgumentProvider;
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
    String message = ((JdaMessagesProvider) context.getMessagesProvider()).guildOnly(context);
    throw new ArgumentProviderException(message);
  }
}
