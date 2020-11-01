package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import me.googas.api.token.AuthLevel;
import org.jetbrains.annotations.NotNull;

/** Provides {@link AuthLevel} in commands */
public class AuthLevelProvider implements JdaArgumentProvider<AuthLevel> {
  @Override
  public @NotNull Class<AuthLevel> getClazz() {
    return AuthLevel.class;
  }

  @NotNull
  @Override
  public AuthLevel fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    try {
      return AuthLevel.valueOf(s.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new ArgumentProviderException(s + " is not a valid auth type");
    }
  }
}
