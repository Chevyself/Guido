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

/** The implementation for authentication in guido */
public class GuidoAuthenticator implements Authenticator {

  /** Each client and its authentication level */
  @NotNull private final HashMap<JsonClientThread, AuthLevel> levels = new HashMap<>();

  /** The required level for each receptor. By default all receptors have read_write */
  @NotNull
  private final HashMap<String, AuthLevel> requiredLevel =
      Maps.builder("auth", AuthLevel.NONE)
          .append("permissions", AuthLevel.READ)
          .append("data-exists", AuthLevel.READ)
          .append("permission", AuthLevel.READ)
          .append("preferences", AuthLevel.READ)
          .append("stats", AuthLevel.READ)
          .build();

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

  /**
   * Authenticates a client
   *
   * @param messenger the client that is authenticating
   * @param token the token that the client used to authenticate
   * @return whether the user was authenticated
   */
  @Receptor(method = "auth")
  public boolean auth(@NotNull JsonMessenger messenger, @ParamName(name = "token") String token) {
    if (messenger instanceof JsonClientThread) {
      AuthToken authToken = Guido.getDataLoader().getAuthToken(token);
      if (authToken != null) {
        levels.put((JsonClientThread) messenger, authToken.getLevel());
        return true;
      }
      return false;
    }
    return false;
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

  /**
   * Disconnects a client
   *
   * @param client the client to disconnect
   * @return true if the client was disconnected
   */
  @Receptor(method = "disconnect")
  public boolean disconnect(@NotNull JsonClientThread client) {
    client.close();
    return true;
  }
}
