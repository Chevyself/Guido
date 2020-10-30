package me.googas.bot.api.events.server;

import me.googas.bot.server.GuidoServer;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a client is disconnected and the server removes it from the server this means that
 * request will be no longer accepted
 */
public class GuidoServerDisconnectionEvent extends GuidoServerEvent {

  /** The client that disconnected from the server */
  @NotNull private final JsonClientThread client;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   * @param client the client that disconnected
   */
  public GuidoServerDisconnectionEvent(
      @NotNull GuidoServer server, @NotNull JsonClientThread client) {
    super(server);
    this.client = client;
  }

  /**
   * Get the client that connected to the server
   *
   * @return the client
   */
  @NotNull
  public JsonClientThread getClient() {
    return this.client;
  }

  @Override
  public String toString() {
    return "GuidoServerDisconnectionEvent{" + "client=" + this.client + "} " + super.toString();
  }
}
