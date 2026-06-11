package dev.xevy.guido.mc;

import com.github.chevyself.starbox.result.Result;
import java.util.Map;
import lombok.NonNull;

public interface MinecraftResultProvider {
  @NonNull
  Result getResult(@NonNull MinecraftPlayer player, @NonNull String key);

  @NonNull
  Result getResult(
      @NonNull MinecraftPlayer player,
      @NonNull String key,
      @NonNull Map<String, String> placeholders);
}
