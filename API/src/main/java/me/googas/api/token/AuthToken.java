package me.googas.api.token;

import me.googas.api.UserData;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Token used by clients to authenticate */
public interface AuthToken extends ICatchable {

  /**
   * Get the token. This must be unique for every token
   *
   * @return the token
   */
  @NotNull
  String getToken();

  /**
   * The id of the user that created the token
   *
   * @return the id of the user that created the token
   */
  @NotNull
  String getUserId();

  /**
   * The user that created the token
   *
   * @return the user that created the token
   */
  @Nullable
  UserData getUser();

  /**
   * Get the level to which the token is authenticated
   *
   * @return the level of authentication
   */
  @NotNull
  AuthLevel getLevel();
}
