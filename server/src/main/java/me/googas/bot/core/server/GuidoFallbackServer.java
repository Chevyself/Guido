package me.googas.bot.core.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import lombok.NonNull;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.api.server.BotServer;
import me.googas.messaging.Request;
import me.googas.messaging.api.Messenger;
import org.jetbrains.annotations.NotNull;

/** A fallback server in case {@link GuidoServer does not work} */
public class GuidoFallbackServer implements BotServer {

  @Override
  public @NonNull BotServer registerHandlers(@NonNull GuidoHandlerRegistry registry) {
    return this;
  }

  @Override
  public @NonNull GuidoAuthenticator getAuthenticator() {
    throw new UnsupportedOperationException("There's no authentication for fallback server");
  }

  @Override
  public @NonNull Set<? extends Messenger> getClients() {
    return new HashSet<>();
  }

  @Override
  public <T> void sendRequest(
      @NotNull Request<T> request, BiConsumer<Messenger, Optional<T>> biConsumer) {}

  @Override
  public @NonNull <T> Map<Messenger, T> sendRequest(@NonNull Request<T> request) {
    return new HashMap<>();
  }

  @Override
  public boolean requiresAuthentication() {
    return true;
  }

  @Override
  public void setRequiresAuthentication(boolean bol) {}

  @Override
  public void start() {}

  @Override
  public void close() {}
}
