package me.googas.bot.api.events.server;

import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.core.server.GuidoServer;
import me.googas.net.sockets.json.server.JsonClientThread;

/** An event called when a connection to the server is made */
public class GuidoServerConnectionEvent extends GuidoServerEvent {

  /** The client that connected to the server */
  @NonNull @Getter private final JsonClientThread client;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   * @param client the client that connected to the server
   */
  public GuidoServerConnectionEvent(@NonNull GuidoServer server, @NonNull JsonClientThread client) {
    super(server);
    this.client = client;
  }

  @Override
  public String toString() {
    return "GuidoServerConnectionEvent{" + "client=" + client + '}';
  }
}
