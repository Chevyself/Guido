package com.starfishst.bukkit.api.events.anticheat;

/** The player to which a player is suspected to be cheating */
public enum SuspectLevel {
  /** The level to which the player must getId automatically banned */
  EXTREME,
  /** The level to which staff must attend now */
  HIGH,
  /** The level to which the player will be kept on watch */
  MEDIUM,
  /** The level to which might be just a false warning */
  LOW
}
