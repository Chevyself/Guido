package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.Guido;
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
    return new DiscordLinkable(Guido.getDataLoader().getDiscordUserData(user.getIdLong()));
  }
}
