package me.googas.guido.receptors.auth;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.net.api.auth.Authenticator;
import me.googas.net.api.messages.Request;
import me.googas.net.sockets.json.JsonMessenger;
import me.googas.net.sockets.json.server.JsonClientThread;

public class SingleTokenAuthentication implements Authenticator<JsonClientThread> {

  @NonNull static final Set<JsonMessenger> authenticated = new HashSet<>();

  @Override
  public boolean isAuthenticated(@NonNull JsonClientThread client, @NonNull Request request) {
    if (request.getMethod().equals("auth")) {
      return true;
    }
    return SingleTokenAuthentication.authenticated.contains(client);
  }
}
