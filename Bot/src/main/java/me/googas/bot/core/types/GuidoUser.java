package me.googas.bot.core.types;

import me.googas.api.user.UserData;
import me.googas.bot.api.events.data.user.UserLoadedDataEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements UserData {

  /** The unique id of the user */
  @NotNull private final String id;

  /**
   * Create the guido user
   *
   * @param id the user id
   * @param addToCache whether to add the user to cache
   */
  public GuidoUser(@NotNull String id, boolean addToCache) {
    super(Time.fromString("5m"), true);
    this.id = id;
    new UserLoadedDataEvent(this).call();
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoUser() {
    this("", false);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new UserUnloadedDataEvent(this).call();
  }

  @Override
  @NotNull
  public String getId() {
    return this.id;
  }

  @Override
  public @NotNull GuidoUser refresh() {
    return (GuidoUser) super.refresh();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoUser)) return false;

    GuidoUser guidoUser = (GuidoUser) o;

    return this.id.equals(guidoUser.id);
  }

  @Override
  public String toString() {
    return "GuidoUser{" + "id='" + this.id + '\'' + "} " + super.toString();
  }
}
