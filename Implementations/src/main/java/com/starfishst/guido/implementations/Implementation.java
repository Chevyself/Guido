package com.starfishst.guido.implementations;

import org.jetbrains.annotations.NotNull;

/** An implementation for the bot */
public interface Implementation {

  /**
   * Get the client of the implementation
   *
   * @return the client
   */
  @NotNull
  ImplementationClient getClient();
}
