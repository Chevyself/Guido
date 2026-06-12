package com.starfishst.bukkit;

import com.github.chevyself.starbox.result.Result;
import dev.xevy.bukkit.lang.BukkitLanguageHandler;
import dev.xevy.guido.mc.MinecraftPlayer;
import dev.xevy.guido.mc.MinecraftResultProvider;
import java.util.Map;
import lombok.NonNull;

public class BukkitResultProvider implements MinecraftResultProvider {
  @NonNull private final BukkitLanguageHandler language;

  public BukkitResultProvider(@NonNull BukkitLanguageHandler language) {
    this.language = language;
  }

  @Override
  public @NonNull Result getResult(@NonNull MinecraftPlayer player, @NonNull String key) {
    return Result.of(language.getFile(player.getLocale()).get(key));
  }

  @Override
  public @NonNull Result getResult(
      @NonNull MinecraftPlayer player,
      @NonNull String key,
      @NonNull Map<String, String> placeholders) {
    return Result.of(language.getFile(player.getLocale()).get(key, placeholders));
  }
}
