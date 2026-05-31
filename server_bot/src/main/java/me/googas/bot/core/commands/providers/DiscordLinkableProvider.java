package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.entities.User;

public class DiscordLinkableProvider implements JdaArgumentProvider<DiscordLinkable> {

  @Override
  public @NonNull Class<DiscordLinkable> getClazz() {
    return DiscordLinkable.class;
  }

  @Override
  public @NonNull DiscordLinkable fromString(
      @NonNull String s, @NonNull CommandContext commandContext) throws ArgumentProviderException {
    User user = commandContext.getProvidersRegistry().fromString(s, User.class, commandContext);
    return Discord.getUser(user);
  }
}
