package com.starfishst.guido.api.data.implementations.receptors;

import com.starfishst.guido.api.data.implementations.ClientImpl;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;

/** Receptors for the implementation */
public class ReceptorsImpl {

  /** The client using the receptors */
  @NotNull private final ClientImpl client;

  /**
   * Create the receptors
   *
   * @param client the client using the receptors
   */
  public ReceptorsImpl(@NotNull ClientImpl client) {
    this.client = client;
  }

  /**
   * Listen if the server disconnected this client
   *
   * @return true if successfully disconnected
   */
  @Receptor(method = "disconnected")
  public boolean disconnected() {
    JsonClient connection = client.getConnection();
    if (connection != null) {
      connection.close();
      client.setConnection(null);
      return true;
    }
    return false;
  }
}
