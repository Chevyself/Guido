package com.starfishst.guido.pgm.listeners.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.starfishst.core.utils.math.MathUtils;
import com.starfishst.core.utils.math.geometry.Point;
import com.starfishst.guido.pgm.api.events.anticheat.SuspectDetectedEvent;
import com.starfishst.guido.pgm.api.events.anticheat.SuspectLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Attempts to detect reach hackers */
public class ReachDetector extends PacketAdapter implements AntiCheatDetector {

  /**
   * Create the reach detector
   *
   * @param plugin the plugin using the detector
   */
  public ReachDetector(@NotNull Plugin plugin) {
    super(plugin, PacketType.Play.Client.USE_ENTITY);
  }

  /**
   * Get a player using its entity id
   *
   * @param id the entity id to match
   * @return the player if the id matches else null
   */
  @Nullable
  public Player getPlayer(int id) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getEntityId() == id) {
        return player;
      }
    }
    return null;
  }

  /**
   * Get the point from a location
   *
   * @param location the location to get the point from
   * @return the point
   */
  @NotNull
  public Point getPoint(@NotNull Location location) {
    return new Point(location.getX(), location.getY(), location.getZ());
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    PacketContainer packet = event.getPacket();
    EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
    if (action == EnumWrappers.EntityUseAction.ATTACK) {
      Player attacked = this.getPlayer(packet.getIntegers().read(0));
      if (attacked != null) {
        Player attacker = event.getPlayer();
        Point attackerEyes = this.getPoint(attacker.getEyeLocation());
        Point attackerFeet = this.getPoint(attacker.getLocation());
        Point attackedFeet = this.getPoint(attacked.getLocation());
        double pitch = attacker.getLocation().getPitch() + 90; // + 90 fixes notches pitch
        double hypotenuse = attackerEyes.distance(attackedFeet);
        double feetToFeet = attackerFeet.distance(attackedFeet);
        double headToFeet = attackerEyes.distance(attackedFeet);
        double k =
            MathUtils.square(feetToFeet)
                - MathUtils.square(headToFeet)
                - MathUtils.square(hypotenuse);
        double theta = Math.acos(k * -1 / hypotenuse * headToFeet);
        double omega = pitch - theta;
        double beta = 180 - theta;
        double alpha = 180 - omega - theta;
        double distance = Math.sin(alpha) * hypotenuse / Math.sin(beta);
        if (distance > 4.0) {
          new SuspectDetectedEvent(
                  attacker,
                  this,
                  "Hit " + attacked.getName() + " from a distance out of bounds: " + distance,
                  SuspectLevel.MEDIUM)
              .call();
        }
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NotNull String getName() {
    return "reach-detector";
  }
}
