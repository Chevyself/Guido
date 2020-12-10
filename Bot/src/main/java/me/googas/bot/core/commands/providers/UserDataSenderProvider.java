package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;

/** Provides the commands with bot user as a command sender */
public class UserDataSenderProvider implements JdaExtraArgumentProvider<UserData> {
  @Override
  public @NonNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NonNull
  @Override
  public UserData getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    UserData user =
        Guido.getDataLoader().getDiscordUserData(context.getSender().getIdLong()).getLinkedUser();
    if (user != null) {
      return user;
    }
    throw new ArgumentProviderException("Internal error");
  }
}
