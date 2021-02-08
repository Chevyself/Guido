package me.googas.api.events.token;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.GuidoEvent;
import me.googas.api.token.AuthToken;
import me.googas.commons.builder.ToStringBuilder;

/** An event that involves an auth token */
public class AuthTokenEvent implements GuidoEvent {

  /** The auth token involved in the event */
  @NonNull @Getter private final AuthToken token;

  /**
   * Create the event
   *
   * @param token the auth token involved in the event
   */
  public AuthTokenEvent(@NonNull AuthToken token) {
    this.token = token;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("token", this.token).build();
  }
}
