package com.starfishst.guido.pgm.api.config;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** Loads data for the guido plugin */
public interface DataLoader {

  /**
   * Get the data of a player using its unique id
   *
   * @param uniqueId the unique id of the player
   * @return the data of the player
   */
  @NotNull
  PlayerData getPlayerData(@NotNull UUID uniqueId);
}
