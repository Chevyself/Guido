package com.starfishst.bukkit.handlers;

import com.starfishst.bukkit.api.events.Handler;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/** Listener for a central spawn location in the world */
public class SpawnHandler implements Handler {

  /** The location where players will spawn */
  @Nullable private Location location = null;

  /**
   * Wait for when a player joins the game to set their spawn location
   *
   * @param event the event of a player spawning after joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoinEvent(PlayerSpawnLocationEvent event) {
    if (this.location != null) event.setSpawnLocation(this.location);
  }

  /**
   * Constructs the location where players will spawn
   *
   * @return the constructed location
   */
  public Location construct() {
    String worldName = this.getSettings().get("world", String.class);
    double x = this.getSettings().getOr("x", Double.class, 0D);
    double y = this.getSettings().getOr("y", Double.class, 0D);
    double z = this.getSettings().getOr("z", Double.class, 0D);
    double yaw = this.getSettings().getOr("yaw", Double.class, 0D);
    double pitch = this.getSettings().getOr("pitch", Double.class, 0D);
    World world = Bukkit.getWorlds().get(0);
    if (worldName != null) {
      World byName = Bukkit.getWorld(worldName);
      if (byName != null) {
        world = byName;
      }
    }
    return new Location(world, x, y, z, (float) yaw, (float) pitch);
  }

  @Override
  public void onEnable() {
    this.location = this.construct();
  }

  /** Called on {@link #unregister()} */
  @Override
  public void onDisable() {}

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @Override
  public @NonNull String getName() {
    return "spawn";
  }
}
