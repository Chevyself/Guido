package com.starfishst.bukkit.handlers.scoreboard;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.handlers.placeholders.PlaceholderHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/** This represents a line in a layout */
public class Line {

  /** The raw string of the line */
  @NotNull @Getter private final String raw;

  /** The position of the line */
  @Getter private final int position;

  /**
   * Create a line
   *
   * @param raw the raw string of the line
   * @param position the position where the line goes
   */
  public Line(@NotNull String raw, int position) {
    this.raw = raw;
    this.position = position;
  }

  /**
   * Parse a layout from a list of strings
   *
   * @param list the list of strings
   * @param reverse whether the list of lines should be reversed to match the minecraft layout
   * @return the parsed layout
   */
  @NotNull
  public static List<Line> parseLayout(@NotNull List<String> list, boolean reverse) {
    List<Line> lines = new ArrayList<>();
    if (reverse) {
      Collections.reverse(list);
    }
    for (int i = 0; i < list.size(); i++) {
      lines.add(Line.parse(list.get(i), i));
    }
    return lines;
  }

  @NonNull
  public static Line parse(@NonNull String raw, int position) {
    if (raw.startsWith("localized:")) {
      return new LocalizedLine(raw.substring(10), position);
    }
    return new Line(raw, position);
  }

  /**
   * Builds this line for a player
   *
   * @param player the player to build this line to
   * @return the built line
   */
  @NotNull
  public String build(@NotNull OfflinePlayer player) {
    String built = this.placeholders().build(player, this.raw);
    return built.length() > 16 ? built.substring(0, 16) : built;
  }

  @NonNull
  public PlaceholderHandler placeholders() {
    return Guido.getHandlerRegistry().requireHandler(PlaceholderHandler.class);
  }
}
