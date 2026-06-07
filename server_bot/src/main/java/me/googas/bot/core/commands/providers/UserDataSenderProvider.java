package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.bot.GuidoBotRuntime;

/** Provides the commands with bot user as a command sender */
public class UserDataSenderProvider implements JdaExtraArgumentProvider<UserData> {
  @NonNull private final GuidoBotRuntime runtime;

  public UserDataSenderProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<UserData> getClazz() {
    return UserData.class;
  }

  @NonNull
  @Override
  public UserData getObject(@NonNull CommandContext context) {
    return runtime.getLoader().getUsers().ensureUserData(context.getSender());
  }
}
