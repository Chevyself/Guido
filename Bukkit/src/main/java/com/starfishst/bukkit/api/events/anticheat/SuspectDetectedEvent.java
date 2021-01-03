package com.starfishst.bukkit.api.events.anticheat;

import com.starfishst.bukkit.api.events.GuidoEvent;
import com.starfishst.bukkit.dependencies.protocol.anticheat.AntiCheatDetector;
import lombok.NonNull;
import org.bukkit.entity.Player;

/** Called when a player is suspected to be using cheats */
public class SuspectDetectedEvent extends GuidoEvent {

  /** The time given in millis when the player was detected */
  private final long sentAt;

  /** The player that is suspected to be using cheats */
  @NonNull private final Player player;

  /** Which detector found out the player is suspected */
  @NonNull private final AntiCheatDetector detector;

  /** The reason to which the player might be cheating */
  @NonNull private final String reason;

  /** The level to which the player is suspected to be cheating */
  @NonNull private final SuspectLevel suspectLevel;

  /**
   * Called when a player is suspected to be cheating
   *
   * @param player the player who might be cheating
   * @param detector the detector who's suspecting of the player
   * @param reason the reason that is player is suspected to be cheating
   * @param suspectLevel the level to which the player is suspected to be cheating
   */
  public SuspectDetectedEvent(
      @NonNull Player player,
      @NonNull AntiCheatDetector detector,
      @NonNull String reason,
      @NonNull SuspectLevel suspectLevel) {
    this.suspectLevel = suspectLevel;
    this.sentAt = System.currentTimeMillis();
    this.player = player;
    this.detector = detector;
    this.reason = reason;
  }

  /**
   * Get when the player was called out for being suspicious
   *
   * @return when this call was made
   */
  public long getSentAt() {
    return this.sentAt;
  }

  /**
   * The player who's suspected to be cheating
   *
   * @return the bukkit player
   */
  @NonNull
  public Player getPlayer() {
    return this.player;
  }

  /**
   * The anti cheat that detected the player
   *
   * @return the anti cheat
   */
  @NonNull
  public AntiCheatDetector getDetector() {
    return this.detector;
  }

  /**
   * The reason why the player might be cheating
   *
   * @return the reason as a string
   */
  @NonNull
  public String getReason() {
    return this.reason;
  }

  /**
   * Get the level to which a player is suspected to be cheating
   *
   * @return the level of suspect
   */
  @NonNull
  public SuspectLevel getSuspectLevel() {
    return this.suspectLevel;
  }
}
