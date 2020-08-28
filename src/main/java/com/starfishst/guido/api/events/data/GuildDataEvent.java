package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This object represents an event which has {@link com.starfishst.guido.api.data.GuildData}
 * involved
 */
public class GuildDataEvent implements GuidoEvent {

  /** The guild data that was involved in the event */
  @NotNull private final GuildData data;

  /**
   * Create the event
   *
   * @param data the guild data that has been loaded
   */
  public GuildDataEvent(@NotNull GuildData data) {
    this.data = data;
  }

  /**
   * Get the guild data that was involved in the event
   *
   * @return the data that was involved in the event
   */
  @NotNull
  public GuildData getData() {
    return data;
  }
}
