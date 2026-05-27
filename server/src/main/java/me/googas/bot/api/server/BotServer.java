package me.googas.bot.api.server;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.net.api.Server;

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
  Optional<GuidoAuthenticator> getAuthenticator();
}
