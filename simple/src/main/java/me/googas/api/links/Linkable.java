package me.googas.api.links;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.GuidoCatchable;
import me.googas.api.Identifiable;
import me.googas.api.Informative;
import me.googas.api.Stateable;
import me.googas.api.economy.Record;
import me.googas.api.events.links.LinkableUnloadedEvent;
import me.googas.api.lang.Localized;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.links.ref.MinecraftLinkable;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Permissible;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;

/** This object represents data that can been linked to an user */
public class Linkable
    implements Permissible,
        Stateable,
        GuidoCatchable,
        Localized,
        Informative,
        Identifiable,
        Record {

  @NonNull @Getter private final LinkableType type;
  @NonNull @Getter private final Map<String, Object> identification;
  @NonNull @Getter private final Map<String, Object> recognition;
  @NonNull @Getter private final Map<String, Double> accounts;
  @NonNull @Getter private final Set<PermissionStack> permissions;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;
  @NonNull @Getter private final Map<String, Map<String, Double>> stats;

  @Getter
  @SerializedName(
      value = "linked-id",
      alternate = {"link", "linked"})
  private String linkedUserId;

  public Linkable(
      @NonNull LinkableType type,
      @NonNull Map<String, Object> identification,
      @NonNull Map<String, Object> recognition,
      @NonNull Map<String, Double> accounts,
      @NonNull Set<PermissionStack> permissions,
      @NonNull Map<String, Map<String, Object>> information,
      @NonNull Map<String, Map<String, Double>> stats,
      String linkedUserId) {
    this.type = type;
    this.identification = identification;
    this.recognition = recognition;
    this.accounts = accounts;
    this.permissions = permissions;
    this.information = information;
    this.stats = stats;
    this.linkedUserId = linkedUserId;
  }

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull LinkableType type, @NonNull Map<String, Object> identification) {
    return this.getInfo().compare(type, identification);
  }

  /**
   * This method is used to compare this linkable data with a type and provided information
   *
   * @param type the type to compare
   * @param identification the identification to compare
   * @param recognition the recognition map to match
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(
      @NonNull LinkableType type,
      @NonNull Map<String, Object> identification,
      @NonNull Map<String, Object> recognition) {
    if (this.getType() != type) return false;
    return this.isSimilar(identification) || this.matches(recognition);
  }

  /**
   * @see #compare(LinkableType, Map<String, Object>)
   * @param info the information of the data comparing
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull LinkableInfo info) {
    return this.getInfo().compare(info);
  }

  /**
   * @see #compare(LinkableType, Map<String, Object>)
   * @param data the other data comparing
   * @return true if it is the same type and the identification matches
   */
  public boolean compare(@NonNull Linkable data) {
    if (this == data) return true;
    return this.compare(data.getInfo());
  }

  public DiscordLinkable toDiscordRef() {
    if (this.getType() != LinkableType.DISCORD) {
      UserData user = this.getLinkedUser();
      if (user != null) {
        Linkable link = user.getLink(LinkableType.DISCORD);
        if (link != null) return link.toDiscordRef();
      }
      return null;
    }
    return new DiscordLinkable(this);
  }

  @NonNull
  public DiscordLinkable requireDiscordRef() {
    return Objects.requireNonNull(this.toDiscordRef(), "Could not getId discord ref");
  }

  @NonNull
  public MinecraftLinkable requireMinecraftRef() {
    return Objects.requireNonNull(this.toMinecraftRef(), "Could not getId minecraft ref");
  }

  public MinecraftLinkable toMinecraftRef() {
    if (this.getType() != LinkableType.MINECRAFT) {
      UserData user = this.getLinkedUser();
      if (user != null) {
        Linkable link = user.getLink(LinkableType.MINECRAFT);
        if (link != null) return link.toMinecraftRef();
      }
      return null;
    }
    return new MinecraftLinkable(this);
  }

  /**
   * Set the linked user to this data
   *
   * @param user the new linked user
   */
  public void setLinkedUser(UserData user) {
    if (user == null) {
      this.linkedUserId = null;
    } else {
      this.linkedUserId = user.getId();
    }
  }

  /**
   * Get this linked data as a single way to identify it. For example in the case of discord it will
   * be the tag, for minecraft its nickname
   *
   * @return a simple way to identify the data
   */
  @NonNull
  public String getSingle() {
    return API.getMessenger().getSingle(this);
  }

  /**
   * Get this linked data but only the type and identification
   *
   * @return the data as uncompleted
   */
  @NonNull
  public LinkableInfo getInfo() {
    return new LinkableInfo(this.type, this.identification, this.stats);
  }

  /**
   * Get all the links to this data. This will look for other links of {@link #getLinkedUser()}
   *
   * @return the collection of connected links
   */
  @NonNull
  public Collection<Linkable> getLinks() {
    UserData user = this.getLinkedUser();
    if (user == null) {
      return new ArrayList<>();
    } else {
      return user.getLinks();
    }
  }

  /**
   * Get whether this data is linked to an user
   *
   * @return true if the data is linked
   */
  public boolean isLinked() {
    return this.getLinkedUser() != null;
  }

  /**
   * Get the team in which the link is on
   *
   * @return the team in which it is on
   */
  public Team getTeam() {
    return API.getLoader().getTeams().getTeam(this);
  }

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  public UserData getLinkedUser() {
    return API.getLoader().getUsers().getUserData(this.linkedUserId);
  }

  @Override
  @NonNull
  public String getLang() {
    return this.getString(null, "lang", "en");
  }

  @Override
  public void sendMessage(@NonNull String message) {
    API.getMessenger().sendMessage(this, message);
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    API.getMessenger().sendLocalized(this, key);
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    API.getMessenger().sendLocalized(this, key, placeholders);
  }

  @Override
  public void setLang(@NonNull String lang) {
    this.setString(null, "lang", lang);
  }

  @Override
  public void onRemove() {
    new LinkableUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Linkable cache() {
    return (Linkable) GuidoCatchable.super.cache();
  }

  @Override
  public String toString() {
    return "Linkable{"
        + "type="
        + type
        + ", identification="
        + identification
        + ", recognition="
        + recognition
        + ", accounts="
        + accounts
        + ", permissions="
        + permissions
        + ", information="
        + information
        + ", stats="
        + stats
        + ", linkedUserId='"
        + linkedUserId
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    Linkable linkable = (Linkable) object;
    return this.type == linkable.type && this.identification.equals(linkable.identification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.identification);
  }
}
