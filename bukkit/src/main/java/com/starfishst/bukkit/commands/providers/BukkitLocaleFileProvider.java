package com.starfishst.bukkit.commands.providers;

import com.github.chevyself.starbox.bukkit.context.CommandContext;
import com.github.chevyself.starbox.bukkit.providers.type.BukkitExtraArgumentProvider;
import com.starfishst.bukkit.Guido;
import dev.xevy.bukkit.BukkitLocaleFile;
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
