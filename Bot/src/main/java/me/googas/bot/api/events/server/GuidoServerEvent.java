package me.googas.bot.api.events.server;

import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.server.GuidoServer;
import org.jetbrains.annotations.NotNull;

/** An event that involves the guido server */
public class GuidoServerEvent implements GuidoEvent {

  /** The guido server which is involved in the event */
  @NotNull private final GuidoServer server;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   */
  public GuidoServerEvent(@NotNull GuidoServer server) {
    this.server = server;
  }

  /**
   * Get the server involved in the event
   *
   * @return the server involved in the event
   */
  @NotNull
  public GuidoServer getServer() {
    return this.server;
  }

  @Override
  public String toString() {
    return "GuidoServerEvent{" + "server=" + this.server + '}';
  }
}
