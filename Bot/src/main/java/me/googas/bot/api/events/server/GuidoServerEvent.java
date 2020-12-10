package me.googas.bot.api.events.server;

import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.core.server.GuidoServer;

/** An event that involves the guido server */
public class GuidoServerEvent implements GuidoEvent {

  /** The guido server which is involved in the event */
  @NonNull private final GuidoServer server;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   */
  public GuidoServerEvent(@NonNull GuidoServer server) {
    this.server = server;
  }

  /**
   * Get the server involved in the event
   *
   * @return the server involved in the event
   */
  @NonNull
  public GuidoServer getServer() {
    return this.server;
  }

  @Override
  public String toString() {
    return "GuidoServerEvent{" + "server=" + this.server + '}';
  }
}
