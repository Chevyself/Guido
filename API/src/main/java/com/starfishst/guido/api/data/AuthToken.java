package com.starfishst.guido.api.data;

import com.starfishst.core.utils.cache.ICatchable;
import org.jetbrains.annotations.NotNull;

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
   * The user that created the token
   *
   * @return the user that created the token
   */
  @NotNull
  UserData getUser();

  /**
   * Get the level to which the token is authenticated
   *
   * @return the level of authentication
   */
  @NotNull
  AuthLevel getLevel();
}
