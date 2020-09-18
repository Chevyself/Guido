package com.starfishst.guido.api.implementations.messaging.json;

import com.starfishst.core.fallback.Fallback;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for socket servers for guido */
public class JsonSocketServer extends Thread implements Runnable {

  /** The actual server socket */
  @NotNull private final ServerSocket server;

  /** The set of clients that are connected to the server */
  @NotNull private final Set<JsonClientThread> clients = new HashSet<>();
  /** The time to timeout requests */
  private final long timeout;

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @throws IOException if the port is already in use
   */
  public JsonSocketServer(int port, long timeout) throws IOException {
    this.server = new ServerSocket(port);
    this.timeout = timeout;
  }

  /**
   * Remove a client from the set of clients
   *
   * @param client the client to remove from the set
   */
  public void remove(@NotNull JsonClientThread client) {
    this.clients.remove(client);
  }

  /**
   * Disconnects a client from the server
   *
   * @param client the client that disconnected
   */
  public void disconnect(@NotNull JsonClientThread client) {
    client.close();
    this.remove(client);
  }

  /**
   * Get the clients that are connected to the server
   *
   * @return the set of clients connected to the server
   */
  @NotNull
  public Set<JsonClientThread> getClients() {
    return clients;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Socket socket = this.server.accept();
        JsonClientThread client = new JsonClientThread(socket, this, this.timeout);
        client.start();
        clients.add(client);
      } catch (IOException e) {
        Fallback.addError("IOException: Socket could not be accepted " + e.getMessage());
        e.printStackTrace();
        break;
      }
    }
  }
}
