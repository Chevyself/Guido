package com.starfishst.bot.server;

import me.googas.messaging.api.Server;
import org.jetbrains.annotations.NotNull;

/** An extension of server */
public interface IGuidoServer extends Server {

  /**
   * Get the guido authenticator of the server
   *
   * @return the guido authenticator
   */
  @NotNull
  GuidoAuthenticator getAuthenticator();
}
