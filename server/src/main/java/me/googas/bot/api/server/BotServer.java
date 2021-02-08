package me.googas.bot.api.server;

import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.messaging.api.Server;

/** An extension of server */
public interface BotServer extends Server {

  @NonNull
  BotServer registerHandlers(@NonNull GuidoHandlerRegistry registry);

  /**
   * Get the guido authenticator of the server
   *
   * @return the guido authenticator
   */
  @NonNull
  GuidoAuthenticator getAuthenticator();
}
