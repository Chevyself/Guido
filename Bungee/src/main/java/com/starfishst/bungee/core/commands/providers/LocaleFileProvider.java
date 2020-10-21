package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.providers.type.BungeeExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Create the locale file provider */
public class LocaleFileProvider implements BungeeExtraArgumentProvider<BungeeLocaleFile> {
  @Override
  public @NotNull Class<BungeeLocaleFile> getClazz() {
    return BungeeLocaleFile.class;
  }

  @NotNull
  @Override
  public BungeeLocaleFile getObject(@NotNull CommandContext commandContext) {
    return Guido.getLanguageHandler().getFile(commandContext);
  }
}
