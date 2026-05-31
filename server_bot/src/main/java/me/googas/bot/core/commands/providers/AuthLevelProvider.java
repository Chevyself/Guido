package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.token.AuthLevel;

/** Provides {@link AuthLevel} in commands */
public class AuthLevelProvider implements JdaArgumentProvider<AuthLevel> {
  @Override
  public @NonNull Class<AuthLevel> getClazz() {
    return AuthLevel.class;
  }

  @NonNull
  @Override
  public AuthLevel fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    try {
      return AuthLevel.valueOf(s.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ArgumentProviderException(s + " is not a valid auth type");
    }
  }
}
