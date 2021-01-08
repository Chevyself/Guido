package com.starfishst.bukkit.handlers.scoreboard;

import com.starfishst.bukkit.api.Guido;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/** A line that is localized */
public class LocalizedLine extends Line {

  /**
   * Create a line
   *
   * @param raw the raw string of the line
   * @param position the position where the line goes
   */
  public LocalizedLine(@NotNull String raw, int position) {
    super(raw, position);
  }

  @Override
  public @NotNull String build(@NotNull OfflinePlayer player) {
    String built =
        this.placeholders()
            .build(player, Guido.getLanguageHandler().getFile(player).get(this.getRaw()));
    return built.length() > 16 ? built.substring(0, 16) : built;
  }
}
