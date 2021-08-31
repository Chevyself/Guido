package me.googas.guido.receptors.auth;

import lombok.NonNull;
import me.googas.net.sockets.json.JsonMessenger;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;

public class AuthReceptors {

  @NonNull private final String token;

  public AuthReceptors(@NonNull String token) {
    this.token = token;
  }

  @Receptor("auth")
  public boolean auth(JsonMessenger messenger, @ParamName("token") String token) {
    if (this.token.equals(token)) {
      SingleTokenAuthentication.authenticated.add(messenger);
      return true;
    }
    return true;
  }
}
