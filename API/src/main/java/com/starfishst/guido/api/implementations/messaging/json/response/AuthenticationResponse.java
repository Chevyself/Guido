package com.starfishst.guido.api.implementations.messaging.json.response;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.data.AuthToken;
import com.starfishst.guido.api.data.loader.DataLoader;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import com.starfishst.guido.api.implementations.messaging.json.JsonClientThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A request made by clients to the server to make them authenticated */
public class AuthenticationResponse implements ResponseGiver<Void> {

  /** The client thread connected to the server that must be authenticated */
  @NotNull private final JsonClientThread client;

  /** The loader to get the token */
  @NotNull private final DataLoader loader;

  /**
   * Create the authentication request
   *
   * @param client the client that must be authenticated
   * @param loader the loader to authenticate the token
   */
  public AuthenticationResponse(@NotNull JsonClientThread client, @NotNull DataLoader loader) {
    this.client = client;
    this.loader = loader;
  }

  @Override
  public @Nullable Response<Void> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    AuthToken token = loader.getAuthToken(request.getParameterOr("token", String.class, "-1"));
    if (token != null) {
      client.setLevel(token.getLevel());
    } else {
      client.sendRequest(
          new VoidRequest(
              "disconnected", Maps.singleton("reason", "Authentication failed. Token not found")));
      client.getServer().disconnect(client);
    }
    return null;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.NONE;
  }
}
