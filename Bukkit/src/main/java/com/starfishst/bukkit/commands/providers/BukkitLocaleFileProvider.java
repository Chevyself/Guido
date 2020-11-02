package com.starfishst.bukkit.commands.providers;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.bukkit.providers.type.BukkitExtraArgumentProvider;
import org.jetbrains.annotations.NotNull;

/** Provides the locale file in commands */
public class BukkitLocaleFileProvider implements BukkitExtraArgumentProvider<BukkitLocaleFile> {
  @NotNull
  @Override
  public BukkitLocaleFile getObject(@NotNull CommandContext context) {
    return Guido.getLanguageHandler().getFile(context);
  }

  @Override
  public @NotNull Class<BukkitLocaleFile> getClazz() {
    return BukkitLocaleFile.class;
  }
}
