package me.googas.bot.api.events.server;

import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.core.server.GuidoServer;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.messaging.json.server.JsonClientThread;

/**
 * Called when a client is disconnected and the server removes it from the server this means that
 * request will be no longer accepted
 */
public class GuidoServerDisconnectionEvent extends GuidoServerEvent {

  /** The client that disconnected from the server */
  @NonNull @Getter private final JsonClientThread client;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   * @param client the client that disconnected
   */
  public GuidoServerDisconnectionEvent(
      @NonNull GuidoServer server, @NonNull JsonClientThread client) {
    super(server);
    this.client = client;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("client", this.client).build();
  }
}
