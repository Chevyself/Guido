package com.starfishst.guido.api.implementations.messaging.json.response;

import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A response for a client when it gets disconnected by the server */
public class DisconnectedResponse implements ResponseGiver<Void> {

  /** The client waiting for disconnection */
  @NotNull private final JsonClient client;

  /**
   * Create the response giver
   *
   * @param client the client that will be disconnected
   */
  public DisconnectedResponse(@NotNull JsonClient client) {
    this.client = client;
  }

  /**
   * Get the response given for certain request
   *
   * @param request the request needing of a response
   * @param messenger the messenger where the request came from
   * @return the response
   */
  @Override
  public @Nullable Response<Void> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    client.close();
    return null;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.NONE;
  }
}
