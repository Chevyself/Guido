package me.googas.api.server;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.loader.Loader;
import me.googas.api.token.AuthLevel;
import me.googas.commons.maps.Maps;
import me.googas.messaging.IRequest;
import me.googas.messaging.json.server.Authenticator;
import me.googas.messaging.json.server.JsonClientThread;

/** The implementation for authentication in guido */
public class GuidoAuthenticator implements Authenticator {

  /** Each client and its authentication level */
  @NonNull @Getter private final HashMap<JsonClientThread, AuthLevel> levels = new HashMap<>();

  /** Each client and its provided information */
  @NonNull @Getter private final HashMap<JsonClientThread, ValuesMap> info = new HashMap<>();

  /** The required level for each receptor. By default all receptors have read_write */
  @NonNull @Getter
  private final Map<String, AuthLevel> requiredLevel = Maps.builder("auth", AuthLevel.NONE).build();

  @NonNull @Getter private final Loader loader;

  public GuidoAuthenticator(@NonNull Loader loader) {
    this.loader = loader;
  }

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
   * Get the bungee client
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
