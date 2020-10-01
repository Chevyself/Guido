package com.starfishst.bot.api.events.data.token;

import com.starfishst.bot.api.events.GuidoEvent;
import com.starfishst.guido.api.data.AuthToken;
import org.jetbrains.annotations.NotNull;

/** An event that involves an auth token */
public class AuthTokenEvent implements GuidoEvent {

  /** The auth token involved in the event */
  @NotNull private final AuthToken token;

  /**
   * Create the event
   *
   * @param token the auth token involved in the event
   */
  public AuthTokenEvent(@NotNull AuthToken token) {
    this.token = token;
  }

  /**
   * Get the auth token involved in the event
   *
   * @return the auth token
   */
  @NotNull
  public AuthToken getToken() {
    return token;
  }
}
