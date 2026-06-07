package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.context.GuildCommandContext;
import com.github.chevyself.starbox.jda.messages.JdaMessagesProvider;
import com.github.chevyself.starbox.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.server.GuidoGuild;

public class GuildDataProvider implements JdaExtraArgumentProvider<GuidoGuild> {

  @NonNull private final GuidoBotRuntime runtime;

    public GuildDataProvider(@NonNull GuidoBotRuntime runtime) {
        this.runtime = runtime;
    }

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
    if (context instanceof GuildCommandContext guildContext) {
      return runtime.getLoader()
              .getGuidoGuildLoader()
          .getGuild(guildContext.getGuild());
    }
    String message = ((JdaMessagesProvider) context.getMessagesProvider()).guildOnly(context);
    throw new ArgumentProviderException(message);
  }
}
