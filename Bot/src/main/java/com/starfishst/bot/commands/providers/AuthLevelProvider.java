package com.starfishst.bot.commands.providers;

import com.starfishst.commands.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.core.providers.type.IArgumentProvider;
import com.starfishst.guido.api.data.AuthLevel;
import org.jetbrains.annotations.NotNull;

public class AuthLevelProvider implements IArgumentProvider<AuthLevel, CommandContext> {
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
