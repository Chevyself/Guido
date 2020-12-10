package me.googas.bot.api.events.data.token;

import lombok.NonNull;
import me.googas.api.token.AuthToken;

/** Called when an auth token gets unloaded */
public class AuthTokenUnloadedEvent extends AuthTokenEvent {
  /**
   * Create the event
   *
   * @param token the auth token involved in the event
   */
  public AuthTokenUnloadedEvent(@NonNull AuthToken token) {
    super(token);
  }
}
