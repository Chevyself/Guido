package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.bungee.providers.type.BungeeExtraArgumentProvider;
import lombok.NonNull;

/** Create the locale file provider */
public class BungeeLocaleFileProvider implements BungeeExtraArgumentProvider<BungeeLocaleFile> {
  @Override
  public @NonNull Class<BungeeLocaleFile> getClazz() {
    return BungeeLocaleFile.class;
  }

  @NonNull
  @Override
  public BungeeLocaleFile getObject(@NonNull CommandContext commandContext) {
    return Guido.getLanguageHandler().getFile(commandContext);
  }
}
