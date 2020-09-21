package com.starfishst.bot.server;

import com.starfishst.guido.api.implementations.messaging.json.JsonSocketServer;
import java.io.IOException;

/** A server for implementations connections */
public class GuidoServer extends JsonSocketServer {

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @throws IOException if the port is already in use
   */
  public GuidoServer(int port, long timeout) throws IOException {
    super(port, timeout);
  }
}
