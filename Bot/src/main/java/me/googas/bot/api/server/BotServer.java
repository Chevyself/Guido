package me.googas.bot.api.server;

import lombok.NonNull;
import me.googas.bot.core.server.GuidoAuthenticator;
import me.googas.messaging.api.Server;

/** An extension of server */
public interface BotServer extends Server {

  /**
   * Get the guido authenticator of the server
   *
   * @return the guido authenticator
   */
  @NonNull
  GuidoAuthenticator getAuthenticator();
}
