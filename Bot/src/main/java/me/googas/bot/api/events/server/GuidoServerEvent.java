package me.googas.bot.api.events.server;

import lombok.Getter;
import lombok.NonNull;
import me.googas.bot.api.events.GuidoEvent;
import me.googas.bot.core.server.GuidoServer;
import me.googas.commons.builder.ToStringBuilder;

/** An event that involves the guido server */
public class GuidoServerEvent implements GuidoEvent {

  /** The guido server which is involved in the event */
  @NonNull @Getter private final GuidoServer server;

  /**
   * Create the event
   *
   * @param server the server involved in the event
   */
  public GuidoServerEvent(@NonNull GuidoServer server) {
    this.server = server;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("server", this.server).build();
  }
}
