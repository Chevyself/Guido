package com.starfishst.guido.pgm.api.events.config;

import com.starfishst.guido.pgm.api.config.PlayerData;
import com.starfishst.guido.pgm.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event that involves the data from a player */
public class PlayerDataEvent extends GuidoEvent {

  /** The player data involved in the event */
  @NotNull private final PlayerData data;

  /**
   * Create the event
   *
   * @param data the player data involved in the event
   */
  public PlayerDataEvent(@NotNull PlayerData data) {
    this.data = data;
  }

  /**
   * Get the player data involved in the event
   *
   * @return the player data involved
   */
  @NotNull
  public PlayerData getData() {
    return data;
  }
}
