package com.starfishst.guido.api.implementations.messaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Gives a response for a request and the given parameters */
public interface ResponseGiver<T> {

  /**
   * Get the response given for certain request
   *
   * @param request the request needing of a response
   * @param messenger the messenger where the request came from
   * @return the response
   */
  @Nullable
  Response<T> getResponse(@NotNull Request request, @NotNull Messenger messenger);
}
