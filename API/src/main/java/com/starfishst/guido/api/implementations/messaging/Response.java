package com.starfishst.guido.api.implementations.messaging;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * This object represents the response of a {@link Request}
 *
 * @param <T> the type of the object with which the response is answering
 */
public class Response<T> implements Message {

  /** The uuid of the request that this is answering to */
  @NotNull private final UUID id;

  /** The object with which the response is responding with */
  @NotNull private final T object;

  /**
   * Create the response
   *
   * @param id the id of the request
   * @param object the object that is used to answer the request
   */
  public Response(@NotNull UUID id, @NotNull T object) {
    this.id = id;
    this.object = object;
  }

  /**
   * Get the object that contains the response
   *
   * @return the object
   */
  @NotNull
  public T getObject() {
    return object;
  }

  /**
   * Get the unique id of the message
   *
   * @return the unique id of the message
   */
  @Override
  public @NotNull UUID getId() {
    return this.id;
  }
}
