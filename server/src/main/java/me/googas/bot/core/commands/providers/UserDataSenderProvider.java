package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaExtraArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.core.util.Discord;

/** Provides the commands with bot user as a command sender */
public class UserDataSenderProvider implements JdaExtraArgumentProvider<UserData> {
  @Override
  public @NonNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NonNull
  @Override
  public UserData getObject(@NonNull CommandContext context) throws ArgumentProviderException {
    UserData user = Discord.getUser(context.getSender()).getLinkedUser();
    if (user != null) {
      return user;
    }
    throw new ArgumentProviderException("Internal error");
  }
}
