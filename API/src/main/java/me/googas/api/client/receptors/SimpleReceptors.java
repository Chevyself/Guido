package me.googas.api.client.receptors;

import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.Client;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;

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
    JsonClient connection = this.client.getConnection();
    if (connection != null) {
      this.client.onDisconnection();
      return true;
    }
    return false;
  }
}
