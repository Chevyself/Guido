package com.starfishst.bukkit.commands.providers;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.guido.api.data.lang.LocaleFile;
import org.jetbrains.annotations.NotNull;

/** Provides the locale file in commands */
public class LocaleFileProvider implements BukkitExtraArgumentProvider<LocaleFile> {
  @NotNull
  @Override
  public LocaleFile getObject(@NotNull CommandContext context) {
    return Guido.getLanguageHandler().getFile(context);
  }

  @Override
  public @NotNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }
}
