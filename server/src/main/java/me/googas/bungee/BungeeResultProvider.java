package me.googas.bungee;

import com.github.chevyself.starbox.common.ComponentResult;
import com.github.chevyself.starbox.result.Result;
import dev.xevy.guido.mc.MinecraftPlayer;
import dev.xevy.guido.mc.MinecraftResultProvider;
import java.util.Map;
import lombok.NonNull;
import me.googas.bungee.lang.BungeeLanguageHandler;

public class BungeeResultProvider implements MinecraftResultProvider {

  @NonNull private final BungeeLanguageHandler language;

  public BungeeResultProvider(@NonNull BungeeLanguageHandler language) {
    this.language = language;
  }

  @Override
  public @NonNull Result getResult(@NonNull MinecraftPlayer player, @NonNull String key) {
    return new ComponentResult(language.getFile(player.getLocale()).getComponent(key));
  }

  @Override
  public @NonNull Result getResult(
      @NonNull MinecraftPlayer player,
      @NonNull String key,
      @NonNull Map<String, String> placeholders) {
    return new ComponentResult(
        language.getFile(player.getLocale()).getComponent(key, placeholders));
  }
}
