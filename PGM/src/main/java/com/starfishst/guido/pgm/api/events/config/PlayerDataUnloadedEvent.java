package com.starfishst.guido.pgm.api.events.config;

import com.starfishst.guido.pgm.api.config.PlayerData;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a player gets unloaded */
public class PlayerDataUnloadedEvent extends PlayerDataEvent {

  /**
   * Create the event
   *
   * @param data the player data involved in the event
   */
  public PlayerDataUnloadedEvent(@NotNull PlayerData data) {
    super(data);
  }
}
