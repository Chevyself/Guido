package me.googas.bot.core.types;

import java.util.Collection;
import java.util.Map;
import me.googas.api.links.LinkableData;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser implements UserData, BotCatchable {

  /** The unique id of the user */
  @NotNull private final String id;

  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;

  /**
   * Create the guido user
   *
   * @param id the user id
   * @param preferences the preferences of the user
   */
  public GuidoUser(@NotNull String id, @NotNull GuidoValuesMap preferences) {
    this.id = id;
    this.preferences = preferences;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoUser() {
    this("", new GuidoValuesMap());
  }

  @Override
  public void sendMessage(@NotNull String message) {
    for (LinkableData link : this.getLinks()) {
      link.sendMessage(message);
      break;
    }
  }

  @Override
  public void sendLocalized(@NotNull String key) {
    for (LinkableData link : this.getLinks()) {
      link.sendLocalized(key);
      break;
    }
  }

  @Override
  public void sendLocalized(@NotNull String key, @NotNull Map<String, String> placeholders) {
    for (LinkableData link : this.getLinks()) {
      link.sendLocalized(key, placeholders);
      break;
    }
  }

  @Override
  public void onRemove() {
    new UserUnloadedDataEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(5, Unit.MINUTES);
  }

  @Override
  @NotNull
  public String getId() {
    return this.id;
  }

  @Override
  public @NotNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public Collection<LinkableData> getLinks() {
    return Guido.getDataLoader().getLinks(this);
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

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NotNull GuidoUser cache() {
    return (GuidoUser) BotCatchable.super.cache();
  }
}
