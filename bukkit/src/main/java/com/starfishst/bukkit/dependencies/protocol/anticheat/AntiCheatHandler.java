package com.starfishst.bukkit.dependencies.protocol.anticheat;

import com.starfishst.bukkit.events.anticheat.SuspectDetectedEvent;
import com.starfishst.bukkit.modules.GuidoModule;
import java.util.HashMap;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/** Listens for warning calls from anti-cheat detectors */
public class AntiCheatHandler implements GuidoModule {

  // FIXME:

  // /** The calls that a player has had */
  // @NotNull private final HashMap<UUID, List<SuspectDetectedEvent>> calls = new HashMap<>();

  /** The calls that have been printed */
  @NonNull private final HashMap<UUID, SuspectDetectedEvent> printed = new HashMap<>();

  /**
   * Listens to suspicious calls from players
   *
   * @param event the event of a player being suspicious
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onSuspectDetected(SuspectDetectedEvent event) {
    /*
    UUID uniqueId = event.getProfile().getUniqueId();
    SuspectDetectedEvent latestPrinted = this.printed.get(uniqueId);
    if (latestPrinted == null
        || event.getDetector() != latestPrinted.getDetector()
        || (event.getSentAt() - latestPrinted.getSentAt()) >= this.getDelay().millis()) {
      this.printed.put(uniqueId, event);
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (player.hasPermission(
            this.getSettings().getOr("permission", String.class, "guido.adminchat"))) {
          String msg =
              BukkitUtils.build(
                  this.getSettings()
                      .getOr(
                          "chat", String.class, "[&6A&r] %player_display% &6cheats &r%command%"));
          MapBuilder<String, String> placeholders =
              Maps.builder("player", event.getProfile().getName())
                  .append("player_display", event.getProfile().getDisplayName())
                  .append("reason", event.getReason());
          player.sendMessage(Strings.build(msg, placeholders));
        }
      }
    }
     */
    // this.getCalls(uniqueId).add(event);
  }

  /*
  /**
   * Get the calls for certain player using its unique id
   *
   * @param uniqueId the unique id of the player
   * @return the suspicions calls of the player
  @NotNull
  public List<SuspectDetectedEvent> getCalls(@NotNull UUID uniqueId) {
    return this.calls.computeIfAbsent(uniqueId, k -> new ArrayList<>());
  }
  */

  /**
   * Get the delay between calls
   *
   * @return the delay between calls
   */
  @NonNull
  public Time getDelay() {
    return new Time(5, Unit.SECONDS);
  }

  @Override
  public void onDisable() {
    // calls.clear();
  }

  @Override
  public @NonNull String getName() {
    return "anti-cheat";
  }
}
