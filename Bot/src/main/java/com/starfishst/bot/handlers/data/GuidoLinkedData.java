package com.starfishst.bot.handlers.data;

import com.google.gson.annotations.SerializedName;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotPermissible;
import com.starfishst.bot.api.events.data.links.LinkedDataLoadedEvent;
import com.starfishst.bot.api.events.data.links.LinkedDataUnloadedEvent;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation of bot linked ata */
public class GuidoLinkedData extends Catchable implements BotLinkedData, BotPermissible {

  /** The type of the linked data */
  @NotNull private final LinkedDataType type;
  /** The way to identify this data */
  @NotNull private final GuidoValuesMap identification;
  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;
  /** The stats of the data */
  @NotNull private final HashMap<String, Double> stats;
  /** The permissions */
  @NotNull private final Set<PermissionStack> permissions;
  /** The id of the linked user */
  @SerializedName("linked-id")
  @Nullable
  private String user;

  /**
   * Create the linked data
   *
   * @param addToCache whether to add it to cache
   * @param type the type of linked data that this is
   * @param user the id of the user linked to this data
   * @param identification the way to identify this data
   * @param preferences the preferences which this data has
   * @param stats the stats of this data
   * @param permissions the permissions of this data
   */
  public GuidoLinkedData(
      boolean addToCache,
      @NotNull LinkedDataType type,
      @Nullable String user,
      @NotNull GuidoValuesMap identification,
      @NotNull GuidoValuesMap preferences,
      @NotNull HashMap<String, Double> stats,
      @NotNull Set<PermissionStack> permissions) {
    super(Time.fromString("3m"), addToCache);
    this.type = type;
    this.user = user;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = permissions;
    if (addToCache) {
      new LinkedDataLoadedEvent(this).call();
    }
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoLinkedData() {
    this(
        false,
        LinkedDataType.NONE,
        null,
        new GuidoValuesMap(),
        new GuidoValuesMap(),
        new HashMap<>(),
        new HashSet<>());
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new LinkedDataUnloadedEvent(this).call();
  }

  @Override
  public @NotNull HashMap<String, Double> getStats() {
    return this.stats;
  }

  @Override
  public @NotNull LinkedDataType getType() {
    return this.type;
  }

  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  @Override
  public @NotNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @Nullable String getLinkedUserId() {
    return this.user;
  }

  @Override
  public void setLinkedUser(@Nullable UserData user) {
    this.user = user == null ? null : user.getId();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof GuidoLinkedData)) return false;

    GuidoLinkedData that = (GuidoLinkedData) object;

    if (this.type != that.type) return false;
    return this.identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = this.type.hashCode();
    result = 31 * result + this.identification.hashCode();
    return result;
  }

  @Override
  public @NotNull GuidoLinkedData refresh() {
    return (GuidoLinkedData) super.refresh();
  }

  @Override
  public @NotNull GuidoLinkedInfo getInfo() {
    return new GuidoLinkedInfo(this.getType(), this.identification);
  }

  @Override
  public boolean isLinked() {
    return this.getLinkedUser() != null;
  }

  @Override
  public String toString() {
    return "GuidoLinkedData{"
        + "type="
        + this.type
        + ", identification="
        + this.identification
        + ", preferences="
        + this.preferences
        + ", stats="
        + this.stats
        + ", permissions="
        + this.permissions
        + ", user='"
        + this.user
        + '\''
        + "} "
        + super.toString();
  }
}
