package me.googas.bot.api.server;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.net.api.Server;
import me.googas.net.sockets.json.server.JsonClientThread;

/** An extension of server */
public interface BotServer extends Server<JsonClientThread> {

  @NonNull
  BotServer registerHandlers(@NonNull GuidoHandlerRegistry registry);

  /**
   * Get the guido authenticator of the server
   *
   * @return the guido authenticator
   */
  Optional<GuidoAuthenticator> getAuthenticator();
}
