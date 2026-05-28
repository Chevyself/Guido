package me.googas.bot.core.server;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.api.server.BotServer;
import me.googas.net.api.Server;
import me.googas.net.api.auth.Authenticator;
import me.googas.net.api.messages.StarboxRequest;
import me.googas.net.sockets.json.server.JsonClientThread;

/** A fallback server in case {@link GuidoServer does not work} */
public class GuidoFallbackServer implements BotServer {

  @Override
  public @NonNull BotServer registerHandlers(@NonNull GuidoHandlerRegistry registry) {
    return this;
  }

  @Override
  public void close() throws IOException {

  }

  @Override
  public <T> void sendRequest(@NonNull StarboxRequest<T> request, BiConsumer<JsonClientThread, Optional<T>> consumer) {

  }

  @Override
  public @NonNull <T> Map<JsonClientThread, Optional<T>> sendRequest(@NonNull StarboxRequest<T> request) {
    return Map.of();
  }

  @Override
  public void start() {

  }

  @Override
  public @NonNull Server<JsonClientThread> setAuthenticator(@NonNull Authenticator<JsonClientThread> authenticator) {
    return this;
  }

  @Override
  public Optional<GuidoAuthenticator> getAuthenticator() {
    return Optional.empty();
  }

  @Override
  public @NonNull Collection<JsonClientThread> getClients() {
    return List.of();
  }
}
