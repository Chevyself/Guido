package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.GuidoBotRuntime;
import net.dv8tion.jda.api.entities.User;

/** Provides bot users in the arguments of a command */
public class UserDataProvider implements JdaArgumentProvider<UserData> {
  @NonNull private final GuidoBotRuntime runtime;

  public UserDataProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NonNull
  @Override
  public UserData fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    User user = context.getProvidersRegistry().fromString(s, User.class, context);
    return runtime.getLoader().getUsers().ensureUserData(user);
  }
}
