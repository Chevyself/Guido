package com.starfishst.bukkit.dependencies.protocol.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.starfishst.bukkit.Guido;
import com.starfishst.bukkit.events.anticheat.SuspectDetectedEvent;
import com.starfishst.bukkit.events.anticheat.SuspectLevel;
import java.util.HashMap;
import java.util.UUID;
import lombok.NonNull;
import me.googas.commons.Lots;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.Plugin;

/** Detects for players using auto-click */
public class AutoClickDetector extends PacketAdapter implements AntiCheatDetector {

  /** The tick tracker to getId the delay between hits */
  @NonNull private final AutoClickTicksTracker tracker;

  /**
   * The uuid and the list containing the clicks of a player. This is the amount of times that the
   * uuid has clicked
   */
  @NonNull private final HashMap<UUID, Integer> clicks = new HashMap<>();

  /**
   * This map contains the unique id of the player and the time that the player has been clicking.
   * It will reset if the player delays the amount set in the configuration 'reset-delay'
   */
  @NonNull private final HashMap<UUID, Integer> time = new HashMap<>();

  /** Whether a player is digging */
  @NonNull private final HashMap<UUID, Boolean> digging = new HashMap<>();

  /** The average speed to which a player is clicking given in clicks per second */
  @NonNull private final HashMap<UUID, Float> averageSpeed = new HashMap<>();

  /** The average deviation to which a player is clicking given in clicks per second */
  @NonNull private final HashMap<UUID, Float> averageDeviation = new HashMap<>();

  /**
   * Create the detector
   *
   * @param plugin the plugin to register the {@link PacketAdapter}
   */
  public AutoClickDetector(@NonNull Plugin plugin) {
    super(
        plugin, Lots.list(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_DIG));
    this.tracker = new AutoClickTicksTracker(plugin, this);
  }

  @Override
  public void onEnable() {
    Guido.getModuleRegistry().engage(this.tracker);
  }

  /**
   * This sets the digging status of a player to false when the gamemode is switched
   *
   * @param event the gamemode of a player changing its gamemode
   */
  @EventHandler
  public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
    this.digging.put(event.getPlayer().getUniqueId(), false);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    PacketType type = event.getPacketType();
    UUID uniqueId = event.getPlayer().getUniqueId();
    if (type == PacketType.Play.Client.BLOCK_DIG) {
      EnumWrappers.PlayerDigType digType = event.getPacket().getPlayerDigTypes().read(0);
      switch (digType) {
        case START_DESTROY_BLOCK:
          if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            this.digging.put(uniqueId, true);
          }
          this.tracker.ticksOnStopped.put(uniqueId, -1);
          break;
        case ABORT_DESTROY_BLOCK:
          this.tracker.ticksOnStopped.put(uniqueId, 4);
          break;
        case DROP_ITEM:
        case DROP_ALL_ITEMS:
        case SWAP_HELD_ITEMS:
        case RELEASE_USE_ITEM:
        case STOP_DESTROY_BLOCK:
          this.tracker.ticksOnStopped.put(uniqueId, 0);
          break;
      }
    } else if (type == PacketType.Play.Client.ARM_ANIMATION) {
      int ticks = this.tracker.getTicks(uniqueId, true);
      if (ticks > this.getSettings().getInt("reset-delay", 50)) {
        this.clicks.put(uniqueId, 0);
        this.time.put(uniqueId, 0);
        this.averageSpeed.put(uniqueId, 0f);
      } else {
        if (event.getPlayer().getGameMode() == GameMode.SURVIVAL
            && !this.digging.getOrDefault(uniqueId, false)) {
          this.time.put(uniqueId, this.time.getOrDefault(uniqueId, 0) + ticks);
          this.clicks.put(uniqueId, this.clicks.getOrDefault(uniqueId, 0) + 1);
          float secs = this.time.get(uniqueId) / 20f;
          float cps = this.clicks.get(uniqueId) / secs;
          float deviation = this.averageSpeed.get(uniqueId) - cps;
          this.averageSpeed.put(uniqueId, cps);
          this.averageDeviation.put(
              uniqueId,
              Math.abs(this.averageDeviation.getOrDefault(uniqueId, 0f) + deviation / cps));
          if (secs > 1) {
            double maxCps = this.getSettings().getDouble("max-cps", 12.0);
            if (cps >= maxCps) {
              new SuspectDetectedEvent(
                      event.getPlayer(),
                      this,
                      "clicking ~" + String.format("%.2f", cps) + "/cps | maximum: " + maxCps,
                      SuspectLevel.MEDIUM)
                  .call();
            }
            float averageDeviation = this.averageDeviation.get(uniqueId);
            Double minDeviation = this.getSettings().getDouble("min-deviation", 0.01);
            if (averageDeviation < minDeviation) {
              new SuspectDetectedEvent(
                      event.getPlayer(),
                      this,
                      "deviation ~"
                          + String.format("%.2f", averageDeviation)
                          + " | min: "
                          + minDeviation,
                      SuspectLevel.LOW)
                  .call();
            }
          }
        }
      }
    }
  }

  @Override
  public void onDisable() {
    Guido.getModuleRegistry().disengage(this.tracker);
    this.clicks.clear();
    this.time.clear();
    this.digging.clear();
    this.averageSpeed.clear();
    this.averageDeviation.clear();
  }

  @Override
  public @NonNull String getName() {
    return "auto-click-detector";
  }

  /** The ticks tracker for the auto click detector */
  static class AutoClickTicksTracker extends TicksTracker {

    /**
     * The amount of ticks a player did after stopping digging. This to archive detect the extra
     * animations when a player stopped digging.
     *
     * <p>If ticks on stopped
     *
     * <p>= -1 the player is not digging = -2 the player has aborted digging
     */
    @NonNull private final HashMap<UUID, Integer> ticksOnStopped = new HashMap<>();

    /** The auto click detector that is using this tracker */
    @NonNull private final AutoClickDetector detector;

    /**
     * Create the ticks tracker
     *
     * @param plugin the plugin that requires track ticks
     * @param detector the auto click detector that is using this tracker
     */
    public AutoClickTicksTracker(@NonNull Plugin plugin, @NonNull AutoClickDetector detector) {
      super(plugin);
      this.detector = detector;
    }

    @Override
    void onIncrease(@NonNull Player player, int amount) {
      UUID uniqueId = player.getUniqueId();
      if (this.ticksOnStopped.getOrDefault(uniqueId, -1) >= 0) {
        this.ticksOnStopped.put(uniqueId, this.ticksOnStopped.get(uniqueId) + amount);
        if (this.ticksOnStopped.get(uniqueId) >= 6) {
          this.ticksOnStopped.put(uniqueId, -1);
          this.detector.digging.put(uniqueId, false);
        }
      }
    }
  }
}
