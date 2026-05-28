package me.googas.api.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.GuidoCatchable;
import me.googas.api.Informative;
import me.googas.api.events.user.UserUnloadedDataEvent;
import me.googas.api.lang.Localized;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;

/** The data of a discord user not required to be in a guild */
public class UserData implements GuidoCatchable, Localized, Informative {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;

  public UserData(@NonNull String id, @NonNull Map<String, Map<String, Object>> information) {
    this.id = id;
    this.information = information;
  }

  public UserData() {
    this("", new HashMap<>());
  }

  public UserData(@NonNull Map<String, Map<String, Object>> information) {
    this(API.getLoader().getUsers().nextUserId(), information);
  }

  /**
   * Get a link to this user from the given type
   *
   * @param type the type of the link to getId
   * @return the link if found else null
   */
  public Linkable getLink(@NonNull LinkableType type) {
    return API.getLoader().getLinks().getLink(this, type);
  }

  /**
   * Get the linkable data that is linked to this user
   *
   * @return the linkable data as a collection
   */
  @NonNull
  public Collection<Linkable> getLinks() {
    return API.getLoader().getLinks().getLinks(this);
  }

  @Override
  @NonNull
  public String getLang() {
    return this.getString(null, "lang", "en");
  }

  @Override
  public void sendMessage(@NonNull String message) {
    for (Linkable link : this.getLinks()) {
      link.sendMessage(message);
    }
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    for (Linkable link : this.getLinks()) {
      link.sendLocalized(key);
    }
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    for (Linkable link : this.getLinks()) {
      link.sendLocalized(key, placeholders);
    }
  }

  @Override
  public void setLang(@NonNull String lang) {
    this.setString(null, "lang", lang);
  }

  @Override
  public void onRemove() {
    new UserUnloadedDataEvent(this).call();
  }

  @Override
  public @NonNull UserData cache() {
    return (UserData) GuidoCatchable.super.cache();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    UserData userData = (UserData) object;
    return this.id.equals(userData.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
