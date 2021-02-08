package me.googas.api.server.receptors;

import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.ValuesMap;
import me.googas.api.server.GuidoAuthenticator;
import me.googas.api.token.AuthToken;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.JsonClientThread;

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

  @Receptor(Requests.Server.CLIENT_INFO)
  public boolean info(@NonNull JsonMessenger messenger, @ParamName("info") ValuesMap info) {
    if (!(messenger instanceof JsonClientThread)) return false;
    this.authenticator.getInfo().put((JsonClientThread) messenger, info);
    return true;
  }

  @Receptor(Requests.Server.AUTH)
  public boolean auth(@NonNull JsonMessenger messenger, @ParamName("token") String token) {
    if (messenger instanceof JsonClientThread) {
      AuthToken authToken = this.authenticator.getLoader().getTokens().getAuthToken(token);
      if (authToken != null) {
        this.authenticator.getLevels().put((JsonClientThread) messenger, authToken.getLevel());
        return true;
      }
      return false;
    }
    return false;
  }
}
