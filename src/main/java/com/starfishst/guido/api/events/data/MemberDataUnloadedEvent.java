package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.MemberData;
import org.jetbrains.annotations.NotNull;

/** Called when the data from a member gets unloaded */
public class MemberDataUnloadedEvent extends MemberDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public MemberDataUnloadedEvent(@NotNull MemberData data) {
    super(data);
  }
}
