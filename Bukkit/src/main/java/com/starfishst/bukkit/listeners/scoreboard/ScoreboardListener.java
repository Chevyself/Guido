package com.starfishst.bukkit.listeners.scoreboard;

import com.starfishst.bukkit.api.events.GuidoListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handles scoreboards */
public class ScoreboardListener implements GuidoListener {

  /** The set of scoreboards */
  @NotNull final Set<GuidoScoreboard> scoreboards = new HashSet<>();

  @NonNull @Getter @Setter private Line defaultTitle = new Line("", 0);

  @NonNull @Getter @Setter private List<Line> layout = new ArrayList<>();

  /**
   * Create a custom scoreboard when a player joins
   *
   * @param event the event when a player joins
   */
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.getScoreboard(event.getPlayer()).initialize(this.getTitle(event.getPlayer()));
  }

  @Nullable
  public GuidoScoreboard getScoreboard(@NonNull OfflinePlayer player) {
    for (GuidoScoreboard scoreboard : this.scoreboards) {
      if (scoreboard.getPlayer().equals(player.getUniqueId())) return scoreboard;
    }
    return null;
  }

  @NonNull
  public GuidoScoreboard getScoreboard(@NonNull Player player) {
    GuidoScoreboard scoreboard = this.getScoreboard((OfflinePlayer) player);
    if (scoreboard != null) return scoreboard;
    return new GuidoScoreboard(player, ScoreboardListener.this.getLayout(player));
  }

  /**
   * Get the title for a player
   *
   * @param player the player to get the title
   * @return the title
   */
  @NotNull
  public String getTitle(@NotNull OfflinePlayer player) {
    return this.defaultTitle.build(player);
  }

  /**
   * Get the layout of the player
   *
   * @param player the player to get the layer
   * @return the layer for the player
   */
  @NotNull
  public List<Line> getLayout(@NotNull @SuppressWarnings("unused") OfflinePlayer player) {
    // TODO in the future could use the player
    return this.layout;
  }

  @Override
  public void register(@NonNull Plugin plugin) {
    this.defaultTitle = Line.parse(this.getSettings().getOr("title", String.class, ""), 0);
    this.layout = Line.parseLayout(this.getSettings().getListValue("layout"), true);
  }

  @Override
  public @NonNull String getName() {
    return "scoreboard";
  }
}
