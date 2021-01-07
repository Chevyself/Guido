package me.googas.bot.core.user;

import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An user that operates this bot */
public class GuidoUser implements UserData, BotCatchable {

  @NonNull private final String id;
  @NonNull private final GuidoValuesMap preferences;

  /**
   * Create the guido user
   *
   * @param id the user id
   * @param preferences the preferences of the user
   */
  public GuidoUser(@NonNull String id, @NonNull GuidoValuesMap preferences) {
    this.id = id;
    this.preferences = preferences;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoUser() {
    this("", new GuidoValuesMap());
  }

  public GuidoUser(@NonNull GuidoValuesMap preferences) {
    this(Guido.getHandlers().getLoader().getUsers().nextUserId(), preferences);
  }

  @Override
  public void sendMessage(@NonNull String message) {
    for (Linkable link : this.getLinks()) {
      link.sendMessage(message);
      break;
    }
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    for (Linkable link : this.getLinks()) {
      link.sendLocalized(key);
      break;
    }
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    for (Linkable link : this.getLinks()) {
      link.sendLocalized(key, placeholders);
      break;
    }
  }

  @Override
  public void onRemove() {
    new UserUnloadedDataEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(5, Unit.MINUTES);
  }

  @Override
  @NonNull
  public String getId() {
    return this.id;
  }

  @Override
  public @NonNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NonNull Collection<Linkable> getLinks() {
    return Guido.getHandlers().getLoader().getLinks().getLinks(this);
  }

  @Override
  public Linkable getLink(@NonNull LinkableType type) {
    return Guido.getHandlers().getLoader().getLinks().getLink(this, type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoUser)) return false;

    GuidoUser guidoUser = (GuidoUser) o;

    return this.id.equals(guidoUser.id);
  }

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NonNull GuidoUser cache() {
    return (GuidoUser) BotCatchable.super.cache();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("preferences", this.preferences)
        .build();
  }
}
