package me.googas.bot.core.token;

import lombok.NonNull;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.token.AuthTokenUnloadedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.commons.RandomUtils;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for {@link AuthToken} */
public class GuidoAuthToken implements AuthToken, BotCatchable {

  @NonNull private final String token;
  @NonNull private final AuthLevel level;
  @NonNull private final String user;

  /**
   * Create the token
   *
   * @param token the unique string of the token
   * @param level the level of authentication of the token
   * @param user the user owner of the token
   */
  public GuidoAuthToken(@NonNull String token, @NonNull AuthLevel level, @NonNull String user) {
    this.token = token;
    this.level = level;
    this.user = user;
  }

  /**
   * Create the token with a brand new string
   *
   * @param level the level of authentication of the token
   * @param user the user owner of the token
   */
  public GuidoAuthToken(@NonNull AuthLevel level, @NonNull String user) {
    this(RandomUtils.nextString(16), level, user);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoAuthToken() {
    this("", AuthLevel.NONE, "");
  }

  @Override
  public void onRemove() {
    new AuthTokenUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(1, Unit.MINUTES);
  }

  @NonNull
  @Override
  public String getToken() {
    return this.token;
  }

  @Override
  public @NonNull String getUserId() {
    return this.user;
  }

  @Override
  public UserData getUser() {
    return Guido.getHandlers().getLoader().getUsers().getUserData(this.user);
  }

  @Override
  public @NonNull AuthLevel getLevel() {
    return this.level;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoAuthToken)) return false;

    GuidoAuthToken that = (GuidoAuthToken) o;

    if (!this.token.equals(that.token)) return false;
    if (this.level != that.level) return false;
    return this.user.equals(that.user);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("token", this.token)
        .append("level", this.level)
        .append("user", this.user)
        .build();
  }

  @Override
  public int hashCode() {
    int result = this.token.hashCode();
    result = 31 * result + this.level.hashCode();
    result = 31 * result + this.user.hashCode();
    return result;
  }

  @Override
  public @NonNull GuidoAuthToken cache() {
    return (GuidoAuthToken) BotCatchable.super.cache();
  }
}
