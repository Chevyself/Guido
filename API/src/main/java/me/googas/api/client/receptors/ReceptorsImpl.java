package me.googas.api.client.receptors;

import me.googas.api.client.Client;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;

/** Receptors for the implementation */
public class ReceptorsImpl {

  /** The client using the receptors */
  @NotNull private final Client client;

  /**
   * Create the receptors
   *
   * @param client the client using the receptors
   */
  public ReceptorsImpl(@NotNull Client client) {
    this.client = client;
  }

  /**
   * Listen if the server disconnected this client
   *
   * @return true if successfully disconnected
   */
  @Receptor("disconnected")
  public boolean disconnected() {
    JsonClient connection = this.client.getConnection();
    if (connection != null) {
      this.client.onDisconnection();
      return true;
    }
    return false;
  }
}
