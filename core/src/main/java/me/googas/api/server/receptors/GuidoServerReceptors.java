package me.googas.api.server.receptors;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.token.AuthToken;
import me.googas.net.sockets.json.JsonMessenger;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.net.sockets.json.server.JsonClientThread;

public class GuidoServerReceptors {

  @NonNull private final GuidoAuthenticator authenticator;

  public GuidoServerReceptors(@NonNull GuidoAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Receptor(Requests.Server.DISCONNECT)
  public boolean disconnect(@NonNull JsonMessenger client) {
    client.close();
    return true;
  }

  @Receptor(Requests.Server.AUTH)
  public boolean auth(@NonNull JsonMessenger messenger, @ParamName("token") String token) {
    if (!(messenger instanceof JsonClientThread)) return false;
    Optional<? extends AuthToken> optional =
        this.authenticator.getLoader().getTokens().getAuthToken(token);
    if (optional.isEmpty()) return false;
    AuthToken authToken = optional.get();
    this.authenticator.getLevels().put((JsonClientThread) messenger, authToken.getAuthLevel());
    return true;
  }
}
