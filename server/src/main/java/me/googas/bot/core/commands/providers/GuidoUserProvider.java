package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.api.utility.Maps;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.Lang;
import net.dv8tion.jda.api.entities.User;

/** Provides bot users in the arguments of a command */
public class GuidoUserProvider implements JdaArgumentProvider<UserData> {
  @Override
  public @NonNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NonNull
  @Override
  public UserData fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    User user = context.getProvidersRegistry().fromString(s, User.class, context);
    UserData userData = Discord.getUser(user).getLinkedUser();
    if (userData != null) {
      return userData;
    }
    throw Lang.getException("not-linked", Maps.singleton("mention", user.getAsMention()), context);
  }
}
