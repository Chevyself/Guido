package com.starfishst.bot.api.events.data.server;

import com.starfishst.bot.server.GuidoServer;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;

/** An event called when a connection to the server is made */
public class GuidoServerConnectionEvent extends GuidoServerEvent {

  /** The client that connected to the server */
  @NotNull private final JsonClientThread client;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   * @param client the client that connected to the server
   */
  public GuidoServerConnectionEvent(@NotNull GuidoServer server, @NotNull JsonClientThread client) {
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
}
