package com.starfishst.guido.implementations.response;

import com.starfishst.guido.api.data.AuthLevel;
import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import com.starfishst.guido.implementations.ImplementationClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisconnectedResponse implements ResponseGiver<Void> {

  /** The implementation client */
  @NotNull private final ImplementationClient client;

  /**
   * The response of the client being disconnected by the server
   *
   * @param client the client to be disconnected
   */
  public DisconnectedResponse(@NotNull ImplementationClient client) {
    this.client = client;
  }

  @Override
  public @Nullable Response<Void> getResponse(
      @NotNull Request<?> request, @NotNull Messenger messenger) {
    JsonClient jsonClient = this.client.getConnection();
    if (jsonClient != null) {
      jsonClient.close();
      this.client.setClient(null);
    }
    return null;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return AuthLevel.NONE;
  }
}
