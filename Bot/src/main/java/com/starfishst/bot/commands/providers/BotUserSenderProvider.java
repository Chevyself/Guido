package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.providers.type.IExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides bot users as command executors */
public class BotUserSenderProvider implements IExtraArgumentProvider<BotUser, CommandContext> {
  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser getObject(@NotNull CommandContext context) {
    return Guido.getDataLoader().getUserData(context.getSender().getIdLong());
  }
}
