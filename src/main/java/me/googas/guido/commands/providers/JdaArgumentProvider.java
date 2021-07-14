package me.googas.guido.commands.providers;

import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.guido.GuidoBot;
import net.dv8tion.jda.api.JDA;

public class JdaArgumentProvider implements JdaExtraArgumentProvider<JDA> {
  @Override
  public @NonNull Class<JDA> getClazz() {
    return JDA.class;
  }

  @Override
  public @NonNull JDA getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    return GuidoBot.getJda();
  }
}
