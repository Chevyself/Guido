package me.googas.api.token;

import lombok.NonNull;
import me.googas.api.user.UserData;
import me.googas.commons.cache.Catchable;

/** Token used by clients to authenticate */
public interface AuthToken extends Catchable {

  /**
   * Get the token. This must be unique for every token
   *
   * @return the token
   */
  @NonNull
  String getToken();

  /**
   * The id of the user that created the token
   *
   * @return the id of the user that created the token
   */
  @NonNull
  String getUserId();

  /**
   * The user that created the token
   *
   * @return the user that created the token
   */
  UserData getUser();

  /**
   * Get the level to which the token is authenticated
   *
   * @return the level of authentication
   */
  @NonNull
  AuthLevel getLevel();
}
