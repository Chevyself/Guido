package com.starfishst.bot.commands.providers;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** Provides with {@link com.starfishst.bot.api.data.BotUser} */
public class BotUserProvider implements IArgumentProvider<BotUser, CommandContext> {

  /** The loader to load the data */
  @NotNull private final BotDataLoader loader;

  /**
   * Create the provider
   *
   * @param loader the data loader to get the data from
   */
  public BotUserProvider(@NotNull BotDataLoader loader) {
    this.loader = loader;
  }

  @Override
  public @NotNull Class<BotUser> getClazz() {
    return BotUser.class;
  }

  @NotNull
  @Override
  public BotUser fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    Object object = context.getRegistry().fromString(s, User.class, context);
    if (object instanceof User) {
      return loader.getUserData(((User) object).getIdLong());
    } else {
      throw new ArgumentProviderException("Provider did not return an user!");
    }
  }
}
