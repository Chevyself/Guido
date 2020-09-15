package com.starfishst.guido.pgm.listeners;

import com.starfishst.bukkit.utils.BukkitUtils;
import com.starfishst.core.utils.Strings;
import com.starfishst.core.utils.maps.Maps;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.pgm.api.events.GuidoListener;
import com.starfishst.guido.pgm.api.events.anticheat.SuspectDetectedEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/** Listens for warning calls from anti-cheat detectors */
public class AntiCheatListener implements GuidoListener {

  /** The calls that a player has had */
  @NotNull private final HashMap<UUID, List<SuspectDetectedEvent>> calls = new HashMap<>();

  /** The calls that have been printed */
  @NotNull private final HashMap<UUID, SuspectDetectedEvent> printed = new HashMap<>();

  /**
   * Listens to suspicious calls from players
   *
   * @param event the event of a player being suspicious
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onSuspectDetected(SuspectDetectedEvent event) {
    UUID uniqueId = event.getPlayer().getUniqueId();
    SuspectDetectedEvent latestPrinted = this.printed.get(uniqueId);
    if (latestPrinted == null
        || event.getDetector() != latestPrinted.getDetector()
        || (event.getSentAt() - latestPrinted.getSentAt()) >= this.getDelay().millis()) {
      this.printed.put(uniqueId, event);
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player.hasPermission(
            this.getSettings().getSettingOr("permission", String.class, "guido.adminchat"))) {
          String msg =
              BukkitUtils.build(
                  this.getSettings()
                      .getSettingOr(
                          "chat", String.class, "[&6A&r] %player_display% &6cheats &r%command%"));
          player.sendMessage(
              Strings.buildMessage(
                  msg,
                  Maps.builder("player", event.getPlayer().getName())
                      .append("player_display", event.getPlayer().getDisplayName())
                      .append("reason", event.getReason())));
        }
      }
    }
    this.getCalls(uniqueId).add(event);
  }

  /**
   * Get the calls for certain player using its unique id
   *
   * @param uniqueId the unique id of the player
   * @return the suspicions calls of the player
   */
  @NotNull
  public List<SuspectDetectedEvent> getCalls(@NotNull UUID uniqueId) {
    return this.calls.computeIfAbsent(uniqueId, k -> new ArrayList<>());
  }

  /**
   * Get the delay between calls
   *
   * @return the delay between calls
   */
  @NotNull
  public Time getDelay() {
    return Time.fromString(this.getSettings().getSettingOr("delay", String.class, "5s"));
  }

  @Override
  public void onUnload() {
    calls.clear();
  }

  @Override
  public @NotNull String getName() {
    return "anti-cheat";
  }
}
