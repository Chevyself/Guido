package com.starfishst.bot.server;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.data.token.AuthToken;
import java.util.HashMap;
import java.util.Map;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.JsonMessenger;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;
import me.googas.messaging.json.server.Authenticator;
import me.googas.messaging.json.server.JsonClientThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation for authentication in guido */
public class GuidoAuthenticator implements Authenticator {

  /** Each client and its authentication level */
  @NotNull private final HashMap<JsonClientThread, AuthLevel> levels = new HashMap<>();

  /** Each client and its provided information */
  @NotNull private final HashMap<JsonClientThread, GuidoValuesMap> info = new HashMap<>();

  /** The required level for each receptor. By default all receptors have read_write */
  @NotNull
  private final HashMap<String, AuthLevel> requiredLevel =
      Maps.builder("auth", AuthLevel.NONE)
          .append("permissions", AuthLevel.READ)
          .append("data-exists", AuthLevel.READ)
          .append("permission", AuthLevel.READ)
          .append("preferences", AuthLevel.READ)
          .append("stats", AuthLevel.READ)
          .append("group", AuthLevel.READ)
          .build();

  /**
   * Removes a client from the levels map
   *
   * @param client the client to remove
   */
  public void remove(@NotNull JsonClientThread client) {
    this.levels.remove(client);
    this.info.remove(client);
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
  @Receptor(method = "client-info")
  public boolean info(@NotNull JsonMessenger messenger, @ParamName(name = "info") Map<?, ?> info) {
    if (messenger instanceof JsonClientThread) {
      GuidoValuesMap map = new GuidoValuesMap();
      info.forEach(
          (key, value) -> {
            if (key instanceof String) {
              if (!((String) key).equalsIgnoreCase("bungee") || this.getBungee() == null) {
                map.addValue((String) key, value);
              }
            }
          });
      this.info.put((JsonClientThread) messenger, map);
      return false;
    }
    return false;
  }

  /**
   * Get the bungee
   *
   * @return the client of the bungee
   */
  @Nullable
  public JsonClientThread getBungee() {
    for (JsonClientThread client : this.info.keySet()) {
      if (this.info.get(client).getValueOr("bungee", Boolean.class, false)) {
        return client;
      }
    }
    return null;
  }

  @Override
  public boolean isAuthenticated(@NotNull JsonClientThread client, @NotNull Request<?> request) {
    if (this.levels.containsKey(client)) {
      AuthLevel authLevel = this.levels.get(client);
      AuthLevel required =
          this.requiredLevel.getOrDefault(request.getMethod(), AuthLevel.READ_WRITE);
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
