package me.googas.bot.api.server;

import me.googas.bot.core.server.GuidoAuthenticator;
import me.googas.messaging.api.Server;
import org.jetbrains.annotations.NotNull;

/** An extension of server */
public interface BotServer extends Server {

  /**
   * Get the guido authenticator of the server
   *
   * @return the guido authenticator
   */
  @NotNull
  GuidoAuthenticator getAuthenticator();
}
