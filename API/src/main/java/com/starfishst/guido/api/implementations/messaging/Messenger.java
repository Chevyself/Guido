package com.starfishst.guido.api.implementations.messaging;

import com.starfishst.guido.api.implementations.messaging.exception.MessengerListenFailException;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/** This object is used to give and receive {@link Request} and {@link Response} */
public interface Messenger {

  /**
   * Sends a request to this messenger
   *
   * @param request the request that was send and must be processed by this messenger
   * @param consumer the consumer to provide the object when the request gets a response
   * @param <T> the type of object that the request expects
   */
  <T> void sendRequest(@NotNull Request<T> request, @NotNull Consumer<T> consumer);

  /**
   * Sends a void request
   *
   * @param request the void request to send
   */
  void sendRequest(@NotNull VoidRequest request);

  /**
   * Listens for incoming requests
   *
   * @throws MessengerListenFailException if the messenger fails to listen to new messages
   */
  void listen() throws MessengerListenFailException;

  /** Closes the messenger */
  void close();
}
