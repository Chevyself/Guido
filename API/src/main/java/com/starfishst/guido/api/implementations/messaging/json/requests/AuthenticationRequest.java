package com.starfishst.guido.api.implementations.messaging.json.requests;

import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** Authenticate a token */
public class AuthenticationRequest extends VoidRequest {

  /**
   * Create the authentication request
   *
   * @param token the token to authenticate
   */
  public AuthenticationRequest(@NotNull String token) {
    super("authenticate", Maps.singleton("token", token));
  }
}
