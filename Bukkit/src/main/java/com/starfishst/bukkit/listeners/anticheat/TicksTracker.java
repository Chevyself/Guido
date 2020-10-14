package com.starfishst.bukkit.listeners.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.starfishst.bukkit.api.events.GuidoPacketListener;
import java.util.HashMap;
import java.util.UUID;
import me.googas.commons.Lots;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/** Tracks the ticks for players */
public abstract class TicksTracker extends PacketAdapter implements GuidoPacketListener {

  /** The amount of ticks that have been sent from the player */
  @NotNull private final HashMap<UUID, Integer> ticks = new HashMap<>();

  /**
   * Create the ticks tracker
   *
   * @param plugin the plugin that requires track ticks
   */
  public TicksTracker(@NotNull Plugin plugin) {
    super(
        plugin,
        Lots.list(
            PacketType.Play.Client.FLYING,
            PacketType.Play.Client.POSITION,
            PacketType.Play.Client.POSITION_LOOK,
            PacketType.Play.Client.LOOK));
  }

  /**
   * Increase the ticks that were sent by the player for certain amount
   *
   * @param uniqueId the unique id to increase the ticks to
   * @param amount the amount of ticks to increase
   */
  public void increase(@NotNull UUID uniqueId, int amount) {
    this.ticks.put(uniqueId, this.ticks.getOrDefault(uniqueId, 0) + amount);
  }

  /**
   * Called when {@link #increase(Player, int)} is invoked
   *
   * @param player the player which got its ticks increased
   * @param amount the amount of ticks that got increased
   */
  abstract void onIncrease(@NotNull Player player, int amount);

  /**
   * Increase the ticks that were sent by the player for certain amount. This will get the UUID of
   * the player an execute {@link #increase(UUID, int)}
   *
   * @param player the player to get the unique id and increase the ticks to
   * @param amount the amount of ticks to increase
   */
  public void increase(@NotNull Player player, int amount) {
    this.onIncrease(player, amount);
    this.increase(player.getUniqueId(), amount);
  }

  /**
   * This sets the ticks sent to 0 for certain UUID
   *
   * @param uniqueId the uuid to set the ticks sent to 0
   */
  public void clear(@NotNull UUID uniqueId) {
    this.ticks.put(uniqueId, 0);
  }

  /**
   * Get the amount of ticks send by an unique id
   *
   * @param uniqueId the unique id to get the ticks from
   * @param clear whether to clear the amount of ticks sent
   * @return the amount of ticks sent with that unique id
   */
  public int getTicks(@NotNull UUID uniqueId, boolean clear) {
    int ticks = this.ticks.getOrDefault(uniqueId, 0);
    if (clear) {
      this.clear(uniqueId);
    }
    return ticks;
  }

  /**
   * Get the amount of ticks send by an unique id
   *
   * @param uniqueId the unique id to get the ticks from
   * @return the amount of ticks sent with that unique id
   */
  public int getTicks(@NotNull UUID uniqueId) {
    return this.getTicks(uniqueId, false);
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    this.increase(event.getPlayer(), 1);
  }

  @Override
  public void onUnload() {
    this.ticks.clear();
  }

  @Override
  public @NotNull String getName() {
    return "tick-tracker";
  }
}
