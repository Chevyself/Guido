package com.starfishst.bot.server;

import com.google.gson.GsonBuilder;
import com.starfishst.bot.server.providers.DataParameterProviders;
import com.starfishst.bot.server.receptors.LinkedDataReceptors;
import com.starfishst.bot.server.receptors.MinecraftDataReceptors;
import com.starfishst.bot.util.console.Console;
import java.io.IOException;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.server.JsonClientThread;
import me.googas.messaging.json.server.JsonSocketServer;
import org.jetbrains.annotations.NotNull;

/** A server for implementations connections */
public class GuidoServer extends JsonSocketServer {

  /** The authentication system for the guido server */
  @NotNull private final GuidoAuthenticator authenticator = new GuidoAuthenticator();

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @throws IOException if the port is already in use
   */
  public GuidoServer(int port, long timeout) throws IOException {
    super(
        port,
        throwable -> Console.exception(throwable, "An exception was cached in the socket server"),
        null,
        new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .setPrettyPrinting()
            .create(),
        timeout);
    this.setAuthenticator(authenticator);
    this.addProviders(new DataParameterProviders());
    this.addReceptors(new LinkedDataReceptors(), new MinecraftDataReceptors(), authenticator);
  }

  @Override
  protected void onRemove(@NotNull JsonClientThread client) {
    authenticator.remove(client);
  }

  @Override
  protected void onConnection(@NotNull JsonClientThread client) {
    authenticator.add(client);
  }
}
