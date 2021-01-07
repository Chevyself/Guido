package me.googas.bot.core.server;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.maps.Maps;
import me.googas.messaging.IRequest;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.Authenticator;
import me.googas.messaging.json.server.JsonClientThread;

/** The implementation for authentication in guido */
public class GuidoAuthenticator implements Authenticator {

  /** Each client and its authentication level */
  @NonNull private final HashMap<JsonClientThread, AuthLevel> levels = new HashMap<>();

  /** Each client and its provided information */
  @NonNull private final HashMap<JsonClientThread, GuidoValuesMap> info = new HashMap<>();

  /** The required level for each receptor. By default all receptors have read_write */
  @NonNull
  private final Map<String, AuthLevel> requiredLevel = Maps.builder("auth", AuthLevel.NONE).build();

  /**
   * Removes a client from the levels map
   *
   * @param client the client to remove
   */
  public void remove(@NonNull JsonClientThread client) {
    this.levels.remove(client);
    this.info.remove(client);
  }

  /**
   * Adds a client to the levels map
   *
   * @param client the client to add
   */
  public void add(@NonNull JsonClientThread client) {
    this.levels.put(client, AuthLevel.NONE);
  }

  /**
   * Authenticates a client
   *
   * @param messenger the client that is authenticating
   * @param token the token that the client used to authenticate
   * @return whether the user was authenticated
   */
  @Receptor("auth")
  public boolean auth(@NonNull JsonMessenger messenger, @ParamName("token") String token) {
    if (messenger instanceof JsonClientThread) {
      AuthToken authToken = Guido.getHandlers().getLoader().getTokens().getAuthToken(token);
      if (authToken != null) {
        this.levels.put((JsonClientThread) messenger, authToken.getLevel());
        return true;
      }
      return false;
    }
    return false;
  }

  /**
   * Provide some information about the client
   *
   * @param messenger the messenger providing info of its self
   * @param info the info of the client
   * @return true if the info was saved
   */
  @Receptor("client-info")
  public boolean info(
      @NonNull JsonMessenger messenger, @ParamName("info") Map<String, Object> info) {
    if (messenger instanceof JsonClientThread) {
      GuidoValuesMap map = new GuidoValuesMap();
      info.forEach(
          (key, value) -> {
            if (!key.equalsIgnoreCase("bungee") || this.getBungee() == null) {
              map.put(key, value);
            }
          });
      this.info.put((JsonClientThread) messenger, map);
      return true;
    }
    return false;
  }

  /**
   * Get the bungee
   *
   * @return the client of the bungee
   */
  public JsonClientThread getBungee() {
    for (JsonClientThread client : this.info.keySet()) {
      if (this.info.get(client).getOr("bungee", Boolean.class, false)) {
        return client;
      }
    }
    return null;
  }

  /**
   * Disconnects a client
   *
   * @param client the client to disconnect
   * @return true if the client was disconnected
   */
  @Receptor("disconnect")
  public void disconnect(@NonNull JsonMessenger client) {
    client.close();
  }

  @Override
  public boolean isAuthenticated(@NonNull JsonClientThread client, @NonNull IRequest request) {
    if (this.levels.containsKey(client)) {
      AuthLevel authLevel = this.levels.get(client);
      AuthLevel required =
          this.requiredLevel.getOrDefault(request.getMethod(), AuthLevel.READ_WRITE);
      return required.intValue() <= authLevel.intValue();
    }
    return false;
  }
}
