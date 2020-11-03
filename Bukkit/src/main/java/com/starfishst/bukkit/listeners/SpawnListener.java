package com.starfishst.bukkit.listeners;

import com.starfishst.bukkit.api.events.GuidoListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/** Listener for a central spawn location in the world */
public class SpawnListener implements GuidoListener {

  /** The location where players will spawn */
  @NotNull private final Location location = this.construct();

  /**
   * Wait for when a player joins the game to set their spawn location
   *
   * @param event the event of a player spawning after joining the game
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerJoinEvent(PlayerSpawnLocationEvent event) {
    event.setSpawnLocation(this.location);
  }

  /**
   * Constructs the location where players will spawn
   *
   * @return the constructed location
   */
  public Location construct() {
    String worldName = this.getSettings().getSetting("world", String.class);
    double x = this.getSettings().getSettingOr("x", Double.class, 0D);
    double y = this.getSettings().getSettingOr("y", Double.class, 0D);
    double z = this.getSettings().getSettingOr("z", Double.class, 0D);
    double yaw = this.getSettings().getSettingOr("yaw", Double.class, 0D);
    double pitch = this.getSettings().getSettingOr("pitch", Double.class, 0D);
    World world = Bukkit.getWorlds().get(0);
    if (worldName != null) {
      World byName = Bukkit.getWorld(worldName);
      if (byName != null) {
        world = byName;
      }
    }
    return new Location(world, x, y, z, (float) yaw, (float) pitch);
  }

  /** Called on {@link #unregister()} */
  @Override
  public void onUnload() {}

  /**
   * Get the name of this listener
   *
   * @return the name of the listener
   */
  @Override
  public @NotNull String getName() {
    return "spawn";
  }
}
