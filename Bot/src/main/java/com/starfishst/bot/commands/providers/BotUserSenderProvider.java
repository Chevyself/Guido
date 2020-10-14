package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the commands with bot user as a command sender */
public class BotUserSenderProvider implements JdaExtraArgumentProvider<BotUser> {
  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser getObject(@NotNull CommandContext context) throws ArgumentProviderException {
    BotUser user =
        Guido.getDataLoader().getDiscordUserData(context.getSender().getIdLong()).getLinkedUser();
    if (user != null) {
      return user;
    }
    throw new ArgumentProviderException("Internal error");
  }
}
