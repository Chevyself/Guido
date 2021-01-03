package com.starfishst.bukkit.dependencies.papi;

import com.starfishst.bukkit.api.events.GuidoListener;
import lombok.NonNull;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PAPIPlaceholderListener implements GuidoListener {
  @NonNull
  public String build(@NonNull OfflinePlayer player, @NonNull String raw) {
    return PlaceholderAPI.setPlaceholders(player, raw);
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "PAPIPlaceholders";
  }
}
