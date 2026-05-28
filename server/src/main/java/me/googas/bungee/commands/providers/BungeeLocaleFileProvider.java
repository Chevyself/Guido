package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeExtraArgumentProvider;
import lombok.NonNull;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.lang.BungeeLocaleFile;

/** Create the locale file provider */
public class BungeeLocaleFileProvider implements BungeeExtraArgumentProvider<BungeeLocaleFile> {
  @Override
  public @NonNull Class<BungeeLocaleFile> getClazz() {
    return BungeeLocaleFile.class;
  }

  @NonNull
  @Override
  public BungeeLocaleFile getObject(@NonNull CommandContext commandContext) {
    return GuidoBungee.getLanguageHandler().getFile(commandContext);
  }
}
