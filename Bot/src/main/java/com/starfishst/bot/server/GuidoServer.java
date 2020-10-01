package com.starfishst.bot.server;

import com.starfishst.bot.api.data.loader.BotDataLoader;
import com.starfishst.bot.server.responses.data.MemberDataResponse;
import com.starfishst.guido.api.implementations.messaging.ResponseGiver;
import com.starfishst.guido.api.implementations.messaging.json.JsonClientThread;
import com.starfishst.guido.api.implementations.messaging.json.JsonSocketServer;
import com.starfishst.guido.api.implementations.messaging.json.response.AuthenticationResponse;
import java.io.IOException;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;

/** A server for implementations connections */
public class GuidoServer extends JsonSocketServer {

  /** The data loader to authenticate requests */
  @NotNull private BotDataLoader loader;

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @param loader the loader to authenticate requests
   * @throws IOException if the port is already in use
   */
  public GuidoServer(int port, long timeout, @NotNull BotDataLoader loader) throws IOException {
    super(port, timeout);
    this.setRequiresAuthentication(true);
    this.loader = loader;
  }

  @Override
  protected void onConnection(@NotNull JsonClientThread client) {
    HashMap<String, ResponseGiver<?>> responseGivers = client.getResponseGivers();
    responseGivers.put("authenticate", new AuthenticationResponse(client, loader));
    responseGivers.put("member-data", new MemberDataResponse(loader));
  }
}
