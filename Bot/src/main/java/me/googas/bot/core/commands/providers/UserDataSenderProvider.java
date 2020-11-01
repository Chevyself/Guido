package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;
import org.jetbrains.annotations.NotNull;

/** Provides the commands with bot user as a command sender */
public class UserDataSenderProvider implements JdaExtraArgumentProvider<UserData> {
  @Override
  public @NotNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NotNull
  @Override
  public UserData getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    UserData user =
        Guido.getDataLoader().getDiscordUserData(context.getSender().getIdLong()).getLinkedUser();
    if (user != null) {
      return user;
    }
    throw new ArgumentProviderException("Internal error");
  }
}
