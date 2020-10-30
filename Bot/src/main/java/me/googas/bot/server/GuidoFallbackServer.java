package me.googas.bot.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import me.googas.messaging.Request;
import me.googas.messaging.api.Messenger;
import me.googas.messaging.json.JsonMessenger;
import org.jetbrains.annotations.NotNull;

/** A fallback server in case {@link GuidoServer does not work} */
public class GuidoFallbackServer implements IGuidoServer {

  @Override
  public @NotNull GuidoAuthenticator getAuthenticator() {
    throw new UnsupportedOperationException("There's no authentication for fallback server");
  }

  @Override
  public @NotNull Set<? extends Messenger> getClients() {
    return new HashSet<>();
  }

  @Override
  public <T> void sendRequest(
      @NotNull Request<T> request, BiConsumer<JsonMessenger, T> biConsumer) {}

  @Override
  public @NotNull <T> HashMap<JsonMessenger, T> sendRequest(@NotNull Request<T> request) {
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
