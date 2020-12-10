package me.googas.api.client;

import lombok.NonNull;
import me.googas.messaging.ThrowableHandler;

/** An implementation for throwable handler */
public class SimpleThrowableHandler implements ThrowableHandler {

  /** The Client that is using the handler */
  @NonNull private final Client client;

  /**
   * Create the throwable handler
   *
   * @param client the client that needs to handle exceptions
   */
  public SimpleThrowableHandler(@NonNull Client client) {
    this.client = client;
  }

  @Override
  public void handle(@NonNull Throwable throwable) {
    // This makes that the connection resets every time there is an exception
    throwable.printStackTrace();
    this.client.disconnect();
  }
}
