package com.starfishst.bukkit.handlers.scoreboard;

import com.starfishst.bukkit.utils.BukkitUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;
import me.googas.commons.maps.Maps;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * The custom scoreboard for a player // TODO separate some of the methods of this class into a
 * interface
 */
public class GuidoScoreboard {

  @NonNull
  private static final Map<Integer, String> characters =
      Maps.builder(10, "a")
          .append(11, "b")
          .append(12, "c")
          .append(13, "d")
          .append(14, "e")
          .append(15, "f")
          .build();
  /** The owner of the custom scoreboard */
  @NonNull @Getter private final UUID player;

  /** The scoreboard */
  @NonNull @Getter private final Scoreboard scoreboard;

  /** The objective of the scoreboard */
  @NonNull @Getter private final Objective objective;

  /** The layout of the scoreboard */
  @NonNull @Setter private List<Line> layout;

  public GuidoScoreboard(
      @NonNull UUID player,
      @NonNull Scoreboard scoreboard,
      @NonNull Objective objective,
      @NonNull List<Line> layout) {
    this.player = player;
    this.scoreboard = scoreboard;
    this.objective = objective;
    this.layout = layout;
  }

  public GuidoScoreboard(
      @NonNull Player player, @NonNull Scoreboard scoreboard, @NonNull List<Line> layout) {
    this(
        player.getUniqueId(),
        scoreboard,
        scoreboard.registerNewObjective(player.getName(), "dummy"),
        layout);
  }

  public GuidoScoreboard(@NonNull Player player, @NonNull List<Line> layout) {
    this(player, Bukkit.getScoreboardManager().getNewScoreboard(), layout);
  }

  /**
   * This is just for creating the scoreboard nothing special. Empty string
   *
   * @param position the amount of spaces that the string should have
   * @return an empty string with spaces
   */
  @NonNull
  public static String getEntryName(int position) {
    if (position > 9) {
      return BukkitUtils.build("&" + position + "&r");
    } else {
      return BukkitUtils.build("&" + GuidoScoreboard.characters.get(position) + "&r");
    }
  }

  @NonNull
  public GuidoScoreboard initialize(@NonNull String title) {
    this.objective.setDisplayName(title);
    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    Player player = this.bukkitPlayer();
    if (player != null) {
      player.setScoreboard(this.scoreboard);
    }
    this.setNewLayout(this.layout);
    return this;
  }

  @NonNull
  public static List<String> divide(@NonNull String string, int length) {
    List<String> split = new ArrayList<>();
    while (string.length() > length) {
      String substring = string.substring(0, 16);
      string = string.substring(length);
      split.add(substring);
    }
    if (!string.isEmpty()) split.add(string);
    return split;
  }

  /**
   * Adds a new line to the scoreboard
   *
   * @param line the line to be added
   * @return the created minecraft team
   */
  @NonNull
  private Team newLine(@NonNull Line line) {
    Team team = this.getLineTeam(line.getPosition());
    String entryName = GuidoScoreboard.getEntryName(line.getPosition());
    if (team == null) {
      team = this.scoreboard.registerNewTeam("line_" + line.getPosition());
      team.addEntry(entryName);
    }
    String build = line.build(this.bukkitOfflinePlayer());
    List<String> divide = GuidoScoreboard.divide(build, 16);
    for (int i = 0; i < divide.size(); i++) {
      String string = divide.get(i);
      switch (i) {
        case 0:
          team.setPrefix(string);
          break;
        case 1:
          team.setDisplayName(string);
          break;
        case 2:
          team.setSuffix(string);
      }
    }
    team.setPrefix(build);
    this.objective.getScore(entryName).setScore(line.getPosition());
    return team;
  }
  /**
   * Gets the line in a position
   *
   * @param position the position to get the line
   * @return a minecraft team representing a line if it exist in the position
   */
  @Nullable
  private Team getLineTeam(int position) {
    return this.scoreboard.getTeam("line_" + position);
  }

  @Nullable
  private Line getLine(int position) {
    for (Line line : this.layout) {
      if (line.getPosition() == position) return line;
    }
    return null;
  }

  /** Updates the scoreboard */
  public void update() {
    this.layout.forEach(this::newLine);
  }

  public void update(int position) {
    Line line = this.getLine(position);
    if (line != null) this.newLine(line);
  }

  /** Destroys this scoreboard */
  public void destroy() {
    this.scoreboard.getTeams().forEach(Team::unregister);
    Player player = this.bukkitPlayer();
    if (player != null) {
      player.setScoreboard(null);
    }
  }

  @Nullable
  private Player bukkitPlayer() {
    return Bukkit.getPlayer(this.player);
  }

  @NonNull
  private OfflinePlayer bukkitOfflinePlayer() {
    return Bukkit.getOfflinePlayer(this.player);
  }

  /**
   * Sets the new layout of the scoreboard
   *
   * @param layout the new layout
   */
  private void setNewLayout(@NonNull List<Line> layout) {
    this.layout = layout;
    Set<Team> editedTeams = new HashSet<>();
    layout.forEach(line -> editedTeams.add(this.newLine(line)));
    this.scoreboard
        .getTeams()
        .forEach(
            team -> {
              if (!editedTeams.contains(team)) {
                team.unregister();
              }
            });
    this.update();
  }

  private void setTitle(@NonNull String title) {
    this.objective.setDisplayName(title);
  }
}
