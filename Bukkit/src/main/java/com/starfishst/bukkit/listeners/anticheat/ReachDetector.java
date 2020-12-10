package com.starfishst.bukkit.listeners.anticheat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.starfishst.bukkit.api.events.anticheat.SuspectDetectedEvent;
import com.starfishst.bukkit.api.events.anticheat.SuspectLevel;
import lombok.NonNull;
import me.googas.commons.math.MathUtils;
import me.googas.commons.math.geometry.Point;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/** Attempts to detect reach hackers */
public class ReachDetector extends PacketAdapter implements AntiCheatDetector {

  /**
   * Create the reach detector
   *
   * @param plugin the plugin using the detector
   */
  public ReachDetector(@NonNull Plugin plugin) {
    super(plugin, PacketType.Play.Client.USE_ENTITY);
  }

  /**
   * Get a player using its entity id
   *
   * @param id the entity id to match
   * @return the player if the id matches else null
   */
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
  @NonNull
  public Point getPoint(@NonNull Location location) {
    return new Point(location.getX(), location.getY(), location.getZ());
  }

  @Override
  public void onPacketReceiving(PacketEvent event) {
    PacketContainer packet = event.getPacket();
    EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
    if (action == EnumWrappers.EntityUseAction.ATTACK) {
      Player attacker = event.getPlayer();
      Player attacked = this.getPlayer(packet.getIntegers().read(0));
      if (attacker.getGameMode() != GameMode.CREATIVE && attacked != null) {
        Point attackerEyes = this.getPoint(attacker.getEyeLocation());
        Point attackerFeet = this.getPoint(attacker.getLocation());
        Point attackedFeet = this.getPoint(attacked.getLocation());
        double pitch = attacker.getLocation().getPitch() + 90; // + 90 fixes notches pitch
        double hypotenuse = attackerEyes.distance(attackedFeet) - 0.6;
        double feetToFeet = attackerFeet.distance(attackedFeet) - 0.6;
        double headToFeet = attackerEyes.distance(attackerFeet);
        double k =
            MathUtils.square(headToFeet)
                - MathUtils.square(feetToFeet)
                + MathUtils.square(hypotenuse);
        double theta = Math.toDegrees(Math.acos(k / (2 * headToFeet * hypotenuse)));
        double omega = 180 - pitch - theta;
        double alpha = 180 - omega - pitch;
        double distance =
            Math.sin(Math.toRadians(alpha)) * hypotenuse / Math.sin(Math.toRadians(pitch));
        if (distance > 4f) {
          new SuspectDetectedEvent(
                  attacker,
                  this,
                  "Hit "
                      + attacked.getName()
                      + " from a distance out of bounds: ~"
                      + String.format("%.2f", distance),
                  SuspectLevel.LOW)
              .call();
        }
      }
    }
  }

  @Override
  public void onUnload() {}

  @Override
  public @NonNull String getName() {
    return "reach-detector";
  }
}
