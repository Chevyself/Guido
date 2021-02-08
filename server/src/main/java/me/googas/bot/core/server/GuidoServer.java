package me.googas.bot.core.server;

import java.io.IOException;
import java.util.logging.Level;
import lombok.CustomLog;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.loader.Loader;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.server.receptors.GroupReceptors;
import me.googas.api.server.receptors.GuidoServerReceptors;
import me.googas.api.server.receptors.LinkReceptors;
import me.googas.api.server.receptors.MatchReceptors;
import me.googas.api.server.receptors.PunishmentReceptors;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.api.events.server.GuidoServerConnectionEvent;
import me.googas.bot.api.events.server.GuidoServerDisconnectionEvent;
import me.googas.bot.api.server.BotServer;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Mongo;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.receptors.BungeeConnectionReceptors;
import me.googas.bungee.receptors.BungeeMessagingReceptors;
import me.googas.bungee.receptors.BungeeQueueReceptors;
import me.googas.bungee.receptors.BungeeReceptors;
import me.googas.messaging.api.Message;
import me.googas.messaging.json.adapters.MessageDeserializer;
import me.googas.messaging.json.server.JsonClientThread;
import me.googas.messaging.json.server.JsonSocketServer;

/** A server for implementations connections */
@CustomLog
public class GuidoServer extends JsonSocketServer implements BotServer {

  /** The authentication system for the guido server */
  @NonNull private final GuidoAuthenticator authenticator;

  /**
   * Creates the guido socket server
   *
   * @param port the port to which the server will be listening to
   * @param timeout the time too timeout requests
   * @throws IOException if the port is already in use
   */
  public GuidoServer(int port, long timeout, @NonNull Loader loader) throws IOException {
    super(
        port,
        throwable ->
            GuidoServer.log.log(
                Level.SEVERE, throwable, () -> "An exception was cached in the socket server"),
        null,
        Mongo.builderGson().registerTypeAdapter(Message.class, new MessageDeserializer()).create(),
        timeout);
    this.authenticator = new GuidoAuthenticator(loader);
    this.setAuthenticator(this.authenticator);
    this.addReceptors(
        new GroupReceptors(loader.getGroups()),
        new GuidoServerReceptors(this.authenticator),
        new LinkReceptors(loader.getLinks()),
        new MatchReceptors(loader.getMatches()),
        new PunishmentReceptors(loader.getPunishments()),
        this.authenticator);
    if (GuidoBungee.isBungee()) {
      this.addReceptors(
          new BungeeConnectionReceptors(),
          new BungeeMessagingReceptors(),
          new BungeeQueueReceptors(),
          new BungeeReceptors());
    }
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
    Requests.Client.disconnected().queue(this);
    super.close();
  }

  @Override
  public @NonNull BotServer registerHandlers(@NonNull GuidoHandlerRegistry registry) {
    for (GuidoHandler handler : registry.getRegistered()) {
      if (handler.hasReceptors()) this.addReceptors(handler);
    }
    return this;
  }

  @NonNull
  @Override
  public GuidoAuthenticator getAuthenticator() {
    return this.authenticator;
  }
}
