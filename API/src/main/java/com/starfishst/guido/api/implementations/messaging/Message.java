package com.starfishst.guido.api.implementations.messaging;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/** A message send between two messengers */
public interface Message {

  /**
   * Get the unique id of the message
   *
   * @return the unique id of the message
   */
  @NotNull
  UUID getId();
}
