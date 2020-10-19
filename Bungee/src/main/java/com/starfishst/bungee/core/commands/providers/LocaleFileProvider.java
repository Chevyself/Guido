package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.providers.type.BungeeExtraArgumentProvider;
import com.starfishst.guido.api.data.lang.LocaleFile;
import org.jetbrains.annotations.NotNull;

/** Create the locale file provider */
public class LocaleFileProvider implements BungeeExtraArgumentProvider<LocaleFile> {
  @Override
  public @NotNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }

  @NotNull
  @Override
  public LocaleFile getObject(@NotNull CommandContext commandContext) {
    return Guido.getLanguageHandler().getFile(commandContext);
  }
}
