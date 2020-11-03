package me.googas.bot.core.types;

import java.util.HashSet;
import java.util.Set;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.events.data.group.GroupUnloadedEvent;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An implementation for the bot group */
public class GuidoGroup extends Catchable implements BotGroup {

  /** The id of the group */
  @NotNull private final String id;

  /** The weight of the group */
  private final int weight;

  /** The name of the group */
  @NotNull private final String name;

  /** The preferences of the group */
  @NotNull private final GuidoValuesMap preferences;

  /** The permissions of the group */
  @NotNull private final Set<PermissionStack> permissions;

  /**
   * Create the group
   *
   * @param id the id of the group
   * @param weight the weight of the group
   * @param name the name of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   * @param addToCache whether to add this group to cache
   */
  public GuidoGroup(
      @NotNull String id,
      int weight,
      @NotNull String name,
      @NotNull GuidoValuesMap preferences,
      @NotNull Set<PermissionStack> permissions,
      boolean addToCache) {
    super(Time.fromString("5m"), false);
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
    this.name = name;
    this.weight = weight;
    if (addToCache) {
      this.addToCache();
    }
  }

  /**
   * Create the group
   *
   * @param name the name of the group
   * @param weight the weight of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   */
  public GuidoGroup(
      int weight,
      @NotNull GuidoValuesMap preferences,
      @NotNull Set<PermissionStack> permissions,
      String name) {
    this(Guido.getDataLoader().nextGroupId(), weight, name, preferences, permissions, true);
  }

  /** @deprecated this may only be used be used by json */
  public GuidoGroup() {
    this("", 1000, "", new GuidoValuesMap(), new HashSet<>(), false);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new GroupUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NotNull String getId() {
    return this.id;
  }

  @Override
  public int getWeight() {
    return this.weight;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @NotNull
  @Override
  public GuidoValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NotNull GuidoGroup refresh() {
    return (GuidoGroup) super.refresh();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoGroup)) return false;

    GuidoGroup that = (GuidoGroup) o;

    if (!this.id.equals(that.id)) return false;
    return this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    int result = this.id.hashCode();
    result = 31 * result + this.name.hashCode();
    return result;
  }
}
