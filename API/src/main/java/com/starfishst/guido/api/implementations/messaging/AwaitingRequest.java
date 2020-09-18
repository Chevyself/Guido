package com.starfishst.guido.api.implementations.messaging;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * A request that is waiting for a {@link Response} in a {@link Messenger}
 *
 * @param <T> the type of object that the request wanted
 */
public class AwaitingRequest<T> {

  /** The request waiting for the response */
  @NotNull private final Request request;

  /** The class of the object requested */
  @NotNull private final Class<T> clazz;

  /** The consumer to execute when the response is received */
  @NotNull private final Consumer<T> consumer;

  /**
   * Create the awaiting request
   *
   * @param request the request that is waiting for a response
   * @param clazz the class of the object that the request is waiting
   * @param consumer the consumer to execute when the response is received
   */
  public AwaitingRequest(
      @NotNull Request request, @NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
    this.request = request;
    this.clazz = clazz;
    this.consumer = consumer;
  }

  /**
   * Get the request waiting for the response
   *
   * @return the request
   */
  @NotNull
  public Request getRequest() {
    return request;
  }

  /**
   * Get the class of the object requested
   *
   * @return the class
   */
  @NotNull
  public Class<T> getClazz() {
    return clazz;
  }

  /**
   * Get the consumer to execute when the response is received
   *
   * @return the consumer
   */
  @NotNull
  public Consumer<T> getConsumer() {
    return consumer;
  }
}
