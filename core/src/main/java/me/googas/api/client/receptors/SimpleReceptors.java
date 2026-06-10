package me.googas.api.client.receptors;

import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.Client;
import me.googas.net.sockets.json.Receptor;

public class SimpleReceptors {

  @NonNull private final Client client;

  /**
   * Create the receptors
   *
   * @param client the client using the receptors
   */
  public SimpleReceptors(@NonNull Client client) {
    this.client = client;
  }

  /**
   * Listen if the server disconnected this client
   *
   * @return true if successfully disconnected
   */
  @Receptor(Requests.Client.DISCONNECTED)
  public boolean disconnected() {
    return this.client
        .getConnection()
        .map(
            connection -> {
              this.client.onDisconnection();
              return true;
            })
        .orElse(false);
  }
}
