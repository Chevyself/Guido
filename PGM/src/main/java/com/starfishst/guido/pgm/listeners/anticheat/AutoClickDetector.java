package com.starfishst.guido.pgm.listeners.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.starfishst.core.utils.Lots;
import com.starfishst.guido.pgm.api.events.anticheat.SuspectDetectedEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** Detects for players using auto-click */
public class AutoClickDetector extends PacketAdapter implements AntiCheatDetector {

  /** The tick tracker to get the delay between hits */
  @NotNull private final TicksTracker tracker;

  /**
   * The uuid and the list containing the clicks of a player. This is the amount of times that the
   * uuid has clicked
   */
  @NotNull private final HashMap<UUID, Integer> clicks = new HashMap<>();

  /**
   * This map contains the unique id of the player and the time that the player has been clicking.
   * It will reset if the player delays the amount set in the configuration 'reset-delay'
   */
  @NotNull private final HashMap<UUID, Integer> time = new HashMap<>();

  /** Whether a player is digging */
  @NotNull private final HashMap<UUID, Boolean> digging = new HashMap<>();

  /** The average speed to which a player is clicking given in clicks per second */
  @NotNull private final HashMap<UUID, Double> averageSpeed = new HashMap<>();

  /** The average deviation to which a player is clicking given in clicks per second */
  @NotNull private final HashMap<UUID, Double> averageDeviation = new HashMap<>();

  /**
   * Create the detector
   *
   * @param plugin the plugin to register the {@link PacketAdapter}
   */
  public AutoClickDetector(@NotNull Plugin plugin) {
    super(
        plugin, Lots.list(PacketType.Play.Client.ARM_ANIMATION, PacketType.Play.Client.BLOCK_DIG));
    this.tracker = new TicksTracker(plugin);
    this.tracker.register(plugin);
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
          break;
        case DROP_ITEM:
        case DROP_ALL_ITEMS:
        case SWAP_HELD_ITEMS:
        case RELEASE_USE_ITEM:
        case STOP_DESTROY_BLOCK:
        case ABORT_DESTROY_BLOCK:
          this.digging.put(uniqueId, false);
          break;
      }
    } else if (type == PacketType.Play.Client.ARM_ANIMATION) {
      int ticks = this.tracker.getTicks(uniqueId, true);
      if (ticks > this.getSettings().getSettingOr("reset-delay", Integer.class, 50)) {
        this.clicks.put(uniqueId, 0);
        this.time.put(uniqueId, 0);
        this.averageSpeed.put(uniqueId, 0.0);
      } else {
        if (!this.digging.getOrDefault(uniqueId, false)) {
          this.time.put(uniqueId, this.time.getOrDefault(uniqueId, 0) + ticks);
          this.clicks.put(uniqueId, this.clicks.getOrDefault(uniqueId, 0) + 1);
          double secs = this.time.get(uniqueId) / 20.0;
          double cps = clicks.get(uniqueId) / secs;
          double deviation = this.averageSpeed.get(uniqueId) - cps;
          this.averageSpeed.put(uniqueId, cps);
          this.averageDeviation.put(
              uniqueId,
              Math.abs(this.averageDeviation.getOrDefault(uniqueId, 0.0) + deviation / cps));
          if (secs > 1) {
            Double maxCps = this.getSettings().getSettingOr("max-cps", Double.class, 12.0);
            if (cps >= maxCps) {
              new SuspectDetectedEvent(
                      event.getPlayer(),
                      this,
                      "clicking " + String.format("%.2f", cps) + "/cps | maximum: " + maxCps)
                  .call();
            }
            Double averageDeviation = this.averageDeviation.get(uniqueId);
            Double minDeviation =
                this.getSettings().getSettingOr("min-deviation", Double.class, 0.01);
            if (averageDeviation < minDeviation) {
              new SuspectDetectedEvent(
                      event.getPlayer(),
                      this,
                      "deviation "
                          + String.format("%.2f", averageDeviation)
                          + " | min: "
                          + minDeviation)
                  .call();
            }
          }
        }
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "auto-click-detector";
  }
}
