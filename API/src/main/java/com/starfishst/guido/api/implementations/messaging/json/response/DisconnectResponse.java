package com.starfishst.guido.api.implementations.messaging.json.response;

import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.JsonClientThread;
import com.starfishst.guido.api.implementations.messaging.json.JsonSocketServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A request for disconnection */
public class DisconnectResponse implements ResponseGiver<Void> {

  /** The server that must disconnect the client */
  @NotNull private final JsonSocketServer server;

  /**
   * Create the response giver
   *
   * @param server the server to disconnect the clients
   */
  public DisconnectResponse(@NotNull JsonSocketServer server) {
    this.server = server;
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
    if (messenger instanceof JsonClientThread) {
      server.disconnect((JsonClientThread) messenger);
    }
    return null;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.NONE;
  }
}
