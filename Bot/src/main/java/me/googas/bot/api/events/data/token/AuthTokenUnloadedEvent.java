package me.googas.bot.api.events.data.token;

import me.googas.api.token.AuthToken;
import org.jetbrains.annotations.NotNull;

/** Called when an auth token gets unloaded */
public class AuthTokenUnloadedEvent extends AuthTokenEvent {
  /**
   * Create the event
   *
   * @param token the auth token involved in the event
   */
  public AuthTokenUnloadedEvent(@NotNull AuthToken token) {
    super(token);
  }
}
