package com.starfishst.bukkit.dependencies.papi;

import com.starfishst.bukkit.api.events.Handler;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PAPIPlaceholderHandler implements Handler {
  @NonNull
  public String build(@NonNull OfflinePlayer player, @NonNull String raw) {
    return PlaceholderAPI.setPlaceholders(player, raw);
  }

  @Override
  public void onDisable() {}

  @Override
  public @NonNull String getName() {
    return "PAPIPlaceholders";
  }
}
