package com.starfishst.bot.api.events.data.token;

import com.starfishst.guido.api.data.token.AuthToken;
import org.jetbrains.annotations.NotNull;

/** Called when an auth token gets loaded */
public class AuthTokenLoadedEvent extends AuthTokenEvent {
  /**
   * Create the event
   *
   * @param token the auth token involved in the event
   */
  public AuthTokenLoadedEvent(@NotNull AuthToken token) {
    super(token);
  }
}
