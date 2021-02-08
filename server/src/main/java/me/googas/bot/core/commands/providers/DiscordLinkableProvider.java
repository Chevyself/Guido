package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
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
    User user = commandContext.get(s, User.class, commandContext);
    return Discord.getUser(user);
  }
}
