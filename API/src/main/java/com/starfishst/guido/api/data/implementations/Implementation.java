package com.starfishst.guido.api.data.implementations;

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
