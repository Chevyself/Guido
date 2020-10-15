package com.starfishst.guido.api.data.implementations;

import me.googas.messaging.ThrowableHandler;
import org.jetbrains.annotations.NotNull;

/** An implementation for throwable handler */
public class ThrowableHandlerImpl implements ThrowableHandler {

  /** The Client that is using the handler */
  @NotNull private final ClientImpl client;

  /**
   * Create the throwable handler
   *
   * @param client the client that needs to handle exceptions
   */
  public ThrowableHandlerImpl(@NotNull ClientImpl client) {
    this.client = client;
  }

  @Override
  public void handle(@NotNull Throwable throwable) {
    // This makes that the connection resets every time there is an exception
    this.client.setConnection(null);
  }
}
