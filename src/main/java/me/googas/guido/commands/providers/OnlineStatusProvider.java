package me.googas.guido.commands.providers;

import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import net.dv8tion.jda.api.OnlineStatus;

public class OnlineStatusProvider implements JdaArgumentProvider<OnlineStatus> {
  @Override
  public @NonNull Class<OnlineStatus> getClazz() {
    return OnlineStatus.class;
  }

  @Override
  public @NonNull OnlineStatus fromString(@NonNull String string, @NonNull CommandContext context)
      throws ArgumentProviderException {
    try {
      return OnlineStatus.valueOf(string.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ArgumentProviderException(string + " is not a valid online status");
    }
  }
}
