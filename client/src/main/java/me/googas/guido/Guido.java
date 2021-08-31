package me.googas.guido;

import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import me.googas.net.sockets.json.client.JsonClient;

public class Guido {

  private static Client client;

  @NonNull
  public static Client getClient() {
    return Objects.requireNonNull(Guido.client, "Client has not been initialized");
  }

  public static void setClient(Client client) {
    if (Guido.client != null && client != null)
      throw new IllegalStateException("Client has been already initialized");
    Guido.client = client;
  }

  public interface Client {

    @NonNull
    Optional<JsonClient> getSocket();
  }
}
