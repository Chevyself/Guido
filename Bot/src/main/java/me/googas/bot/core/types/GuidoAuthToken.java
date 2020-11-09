package me.googas.bot.core.types;

import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.api.events.data.token.AuthTokenUnloadedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.Guido;
import me.googas.commons.RandomUtils;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An implementation for {@link AuthToken} */
public class GuidoAuthToken implements AuthToken, BotCatchable {

  /** The unique string token */
  @NotNull private final String token;

  /** The level of authentication */
  @NotNull private final AuthLevel level;

  /** The user owner of the token */
  @NotNull private final String user;

  /**
   * Create the token
   *
   * @param token the unique string of the token
   * @param level the level of authentication of the token
   * @param user the user owner of the token
   * @param addToCache whether to add the token to cache
   */
  public GuidoAuthToken(
      @NotNull String token, @NotNull AuthLevel level, @NotNull String user, boolean addToCache) {
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
  public GuidoAuthToken(@NotNull AuthLevel level, @NotNull String user) {
    this(RandomUtils.nextString(16), level, user, true);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoAuthToken() {
    this("", AuthLevel.NONE, "", false);
  }

  @Override
  public void onRemove() {
    new AuthTokenUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(1, Unit.MINUTES);
  }

  @NotNull
  @Override
  public String getToken() {
    return this.token;
  }

  @Override
  public @NotNull String getUserId() {
    return this.user;
  }

  @Override
  public @Nullable UserData getUser() {
    return Guido.getDataLoader().getUserData(this.user);
  }

  @Override
  public @NotNull AuthLevel getLevel() {
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
  public int hashCode() {
    int result = this.token.hashCode();
    result = 31 * result + this.level.hashCode();
    result = 31 * result + this.user.hashCode();
    return result;
  }

  @Override
  public @NotNull GuidoAuthToken cache() {
    return (GuidoAuthToken) BotCatchable.super.cache();
  }
}
