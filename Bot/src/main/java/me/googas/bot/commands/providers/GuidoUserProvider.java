package me.googas.bot.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import me.googas.api.UserData;
import me.googas.bot.Guido;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** Provides bot users in the arguments of a command */
public class GuidoUserProvider implements JdaArgumentProvider<UserData> {
  @Override
  public @NotNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NotNull
  @Override
  public UserData fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Object obj = context.getRegistry().fromString(s, User.class, context);
    if (obj instanceof User) {
      UserData user =
          Guido.getDataLoader().getDiscordUserData(((User) obj).getIdLong()).getLinkedUser();
      if (user != null) {
        return user;
      }
      throw new ArgumentProviderException(
          Guido.getLanguageHandler()
              .getFile(context)
              .get("not-linked", Maps.singleton("mention", ((User) obj).getAsMention())));
    } else {
      throw new ArgumentProviderException("Context did not return an user");
    }
  }
}
