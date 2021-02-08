package com.starfishst.bukkit.commands.providers;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.lang.BukkitLocaleFile;
import com.starfishst.commands.bukkit.context.CommandContext;
import com.starfishst.commands.bukkit.providers.type.BukkitExtraArgumentProvider;
import lombok.NonNull;

/** Provides the locale file in commands */
public class BukkitLocaleFileProvider implements BukkitExtraArgumentProvider<BukkitLocaleFile> {
  @NonNull
  @Override
  public BukkitLocaleFile getObject(@NonNull CommandContext context) {
    return Guido.getLanguageHandler().getFile(context);
  }

  @Override
  public @NonNull Class<BukkitLocaleFile> getClazz() {
    return BukkitLocaleFile.class;
  }
}
