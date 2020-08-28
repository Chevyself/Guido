package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.events.GuidoEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An event that involves the data from a member {@link com.starfishst.guido.api.data.MemberData}
 */
public class MemberDataEvent implements GuidoEvent {

  /** The data of the member involved */
  @NotNull private final MemberData data;

  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public MemberDataEvent(@NotNull MemberData data) {
    this.data = data;
  }

  /**
   * Get the member data involved in the event
   *
   * @return the member data involved in the event
   */
  @NotNull
  public MemberData getData() {
    return data;
  }
}
