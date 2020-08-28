package com.starfishst.guido.api.events.data;

import com.starfishst.guido.api.data.MemberData;
import org.jetbrains.annotations.NotNull;

/** Called when the data of a member gets loaded */
public class MemberDataLoadedEvent extends MemberDataEvent {
  /**
   * Create the event
   *
   * @param data the data involved in the event
   */
  public MemberDataLoadedEvent(@NotNull MemberData data) {
    super(data);
  }
}
