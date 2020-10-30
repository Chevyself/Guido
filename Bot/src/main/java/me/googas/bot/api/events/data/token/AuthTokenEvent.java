package me.googas.bot.api.events.data.token;

import me.googas.api.token.AuthToken;
import me.googas.bot.api.events.GuidoEvent;
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
    return this.token;
  }

  @Override
  public String toString() {
    return "AuthTokenEvent{" + "token=" + this.token + '}';
  }
}
