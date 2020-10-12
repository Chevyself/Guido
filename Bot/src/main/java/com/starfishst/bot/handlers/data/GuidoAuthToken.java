package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.events.data.token.AuthTokenLoadedEvent;
import com.starfishst.bot.api.events.data.token.AuthTokenUnloadedEvent;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.data.token.AuthToken;
import me.googas.commons.RandomUtils;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link AuthToken} */
public class GuidoAuthToken extends Catchable implements AuthToken {

  /** The unique string token */
  @NotNull private final String token;

  /** The level of authentication */
  @NotNull private final AuthLevel level;

  /** The user owner of the token */
  @NotNull private final UserData user;

  /**
   * Create the token
   *
   * @param token the unique string of the token
   * @param level the level of authentication of the token
   * @param user the user owner of the token
   */
  public GuidoAuthToken(@NotNull String token, @NotNull AuthLevel level, @NotNull UserData user) {
    super(new Time(1, Unit.MINUTES));
    this.token = token;
    this.level = level;
    this.user = user;
    new AuthTokenLoadedEvent(this).call();
  }

  /**
   * Create the token with a brand new string
   *
   * @param level the level of authentication of the token
   * @param user the user owner of the token
   */
  public GuidoAuthToken(@NotNull AuthLevel level, @NotNull UserData user) {
    this(RandomUtils.nextString(16), level, user);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new AuthTokenUnloadedEvent(this).call();
  }

  @NotNull
  @Override
  public String getToken() {
    return this.token;
  }

  @Override
  public @NotNull UserData getUser() {
    return this.user;
  }

  @Override
  public @NotNull AuthLevel getLevel() {
    return this.level;
  }
}
