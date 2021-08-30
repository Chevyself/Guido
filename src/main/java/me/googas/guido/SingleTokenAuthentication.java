package me.googas.guido;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.net.api.auth.Authenticator;
import me.googas.net.api.messages.StarboxRequest;
import me.googas.net.sockets.json.JsonMessenger;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.net.sockets.json.server.JsonClientThread;

public class SingleTokenAuthentication implements Authenticator<JsonClientThread> {

  @NonNull private static final Set<JsonMessenger> authenticated = new HashSet<>();

  @Override
  public boolean isAuthenticated(
      @NonNull JsonClientThread messenger, @NonNull StarboxRequest starboxRequest) {
    if (starboxRequest.getMethod().equals("auth")) {
      return true;
    }
    return SingleTokenAuthentication.authenticated.contains(messenger);
  }

  public static class Receptors {

    @NonNull private final String token;

    public Receptors(@NonNull String token) {
      this.token = token;
    }

    @Receptor("auth")
    public boolean auth(JsonMessenger messenger, @ParamName("token") String token) {
      if (this.token.equals(token)) {
        SingleTokenAuthentication.authenticated.add(messenger);
        return true;
      }
      return false;
    }
  }
}
