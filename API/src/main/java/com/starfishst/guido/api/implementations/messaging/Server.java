package com.starfishst.guido.api.implementations.messaging;

import java.io.IOException;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** A server {@link Messenger} can connect to */
public interface Server {

  /**
   * Closes the server
   *
   * @throws IOException some objects when closed can cause this exception
   */
  void close() throws IOException;

  /**
   * Get the clients that are connected to the server
   *
   * @return the set of clients connected to the server
   */
  @NotNull
  Set<? extends Messenger> getClients();

  /**
   * Whether clients need authentication to use
   *
   * @return the
   */
  boolean requiresAuthentication();

  /** Makes the server start listening */
  void start();

  /**
   * Set whether clients must be authenticated
   *
   * @param bol the new value whether clients need authentication
   */
  void setRequiresAuthentication(boolean bol);
}
