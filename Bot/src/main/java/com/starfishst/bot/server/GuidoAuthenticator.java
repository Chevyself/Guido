package com.starfishst.bot.server;

import com.starfishst.bot.Guido;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.data.token.AuthToken;
import java.util.HashMap;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.Authenticator;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;

public class GuidoAuthenticator implements Authenticator {

  @NotNull private final HashMap<JsonClientThread, AuthLevel> levels = new HashMap<>();

  @NotNull
  private final HashMap<String, AuthLevel> requiredLevel =
      Maps.builder("auth", AuthLevel.NONE).append("permissions", AuthLevel.READ).build();

  /**
   * Removes a client from the levels map
   *
   * @param client the client to remove
   */
  public void remove(@NotNull JsonClientThread client) {
    this.levels.remove(client);
  }

  /**
   * Adds a client to the levels map
   *
   * @param client the client to add
   */
  public void add(@NotNull JsonClientThread client) {
    this.levels.put(client, AuthLevel.NONE);
  }

  @Receptor(method = "auth")
  public String auth(JsonMessenger messenger, @ParamName(name = "token") String token) {
    if (messenger instanceof JsonClientThread) {
      AuthToken authToken = Guido.getDataLoader().getAuthToken(token);
      if (authToken != null) {
        levels.put((JsonClientThread) messenger, authToken.getLevel());
        return "authenticated";
      }
      return "token-not-found";
    }
    return "internal-error";
  }

  @Override
  public boolean isAuthenticated(@NotNull JsonClientThread client, @NotNull Request<?> request) {
    if (levels.containsKey(client)) {
      AuthLevel authLevel = levels.get(client);
      AuthLevel required = requiredLevel.getOrDefault(request.getMethod(), AuthLevel.READ_WRITE);
      return required.intValue() <= authLevel.intValue();
    }
    return false;
  }
}
