package com.starfishst.guido.api.implementations.messaging;

import java.io.IOException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** A server {@link Messenger} can connect to */
public interface Server {

  /** Closes the server */
  void close() throws IOException;

  /**
   * Get the clients that are connected to the server
   *
   * @return the set of clients connected to the server
   */
  @NotNull
  Set<? extends Messenger> getClients();
}
