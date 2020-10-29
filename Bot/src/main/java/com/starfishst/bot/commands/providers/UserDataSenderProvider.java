package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.core.exceptions.ArgumentProviderException;
import me.googas.api.UserData;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
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
