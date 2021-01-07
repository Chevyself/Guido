package me.googas.bot.core.server;

import java.io.IOException;
import java.util.logging.Level;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.bot.api.events.server.GuidoServerConnectionEvent;
import me.googas.bot.api.events.server.GuidoServerDisconnectionEvent;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.server.receptors.handlers.LinkReceptors;
import me.googas.bot.core.util.Mongo;
import me.googas.messaging.Request;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.server.JsonClientThread;
import me.googas.messaging.json.server.JsonSocketServer;

/** A server for implementations connections */
@CustomLog
public class GuidoServer extends JsonSocketServer implements BotServer {

  /** The authentication system for the guido server */
  @NonNull private final GuidoAuthenticator authenticator = new GuidoAuthenticator();

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
        throwable ->
            GuidoServer.log.log(
                Level.SEVERE, throwable, () -> "An exception was cached in the socket server"),
        null,
        Mongo.builderGson(false)
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create(),
        timeout);
    this.setAuthenticator(this.authenticator);
    this.addReceptors(new LinkReceptors(), this.authenticator);
  }

  @Override
  protected void onRemove(@NonNull JsonClientThread client) {
    this.authenticator.remove(client);
    new GuidoServerDisconnectionEvent(this, client).call();
  }

  @Override
  protected void onConnection(@NonNull JsonClientThread client) {
    this.authenticator.add(client);
    new GuidoServerConnectionEvent(this, client).call();
  }

  @Override
  public void close() throws IOException {
    this.sendRequest(new Request<>(Boolean.class, "disconnected"), (client, disconnected) -> {});
    super.close();
  }

  @NonNull
  @Override
  public GuidoAuthenticator getAuthenticator() {
    return this.authenticator;
  }
}
