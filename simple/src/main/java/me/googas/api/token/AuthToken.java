package me.googas.api.token;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.GuidoCatchable;
import me.googas.api.events.token.AuthTokenUnloadedEvent;
import me.googas.api.user.UserData;
import me.googas.api.utility.RandomUtils;

/** Token used by clients to authenticate */
public class AuthToken implements GuidoCatchable {

  @NonNull @Getter private final String token;
  @NonNull @Getter private final String userId;
  @NonNull @Getter private final AuthLevel level;

  /**
   * Create the token
   *
   * @param token the actual token. This must be unique for every token
   * @param userId The id of the user that created the token
   * @param level the level to which the token is authenticated
   */
  public AuthToken(@NonNull String token, @NonNull String userId, @NonNull AuthLevel level) {
    this.token = token;
    this.userId = userId;
    this.level = level;
  }

  /** @deprecated this constructor may only be used by GSON */
  public AuthToken() {
    this("", "", AuthLevel.NONE);
  }

  public AuthToken(@NonNull String userId, @NonNull AuthLevel level) {
    this(RandomUtils.nextString(16), userId, level);
  }

  /**
   * The user that created the token
   *
   * @return the user that created the token
   */
  public UserData getUser() {
    return API.getLoader().getUsers().getUserData(this.userId);
  }

  @Override
  public void onRemove() {
    new AuthTokenUnloadedEvent(this).call();
  }

  @Override
  public @NonNull AuthToken cache() {
    return (AuthToken) GuidoCatchable.super.cache();
  }
}
