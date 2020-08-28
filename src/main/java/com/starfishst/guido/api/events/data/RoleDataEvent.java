package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.RoleData;
import com.starfishst.guido.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/** An event where the data of a role is involved */
public class RoleDataEvent implements GuidoEvent {

  /** The data involved in the event */
  @NotNull private final RoleData data;

  /**
   * Create the event
   *
   * @param data the role data involved in the event
   */
  public RoleDataEvent(@NotNull RoleData data) {
    this.data = data;
  }

  /**
   * Get the role data involved in the event
   *
   * @return the role data involved in the event
   */
  @NotNull
  public RoleData getData() {
    return data;
  }
}
