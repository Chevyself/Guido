package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;
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
    User user = context.get(s, User.class, context);
    UserData userData = Guido.getDataLoader().getDiscordUserData(user.getIdLong()).getLinkedUser();
    if (userData != null) {
      return userData;
    }
    throw Lang.getException("not-linked", Maps.singleton("mention", user.getAsMention()), context);
  }
}
