package me.googas.bot.core.types;

import java.util.HashSet;
import java.util.Set;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.events.data.group.GroupUnloadedEvent;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.jetbrains.annotations.NotNull;

/** An implementation for the bot group */
public class GuidoGroup implements BotGroup {

  /** The id of the group */
  @NotNull private final String id;

  /** The weight of the group */
  private int weight;

  /** The name of the group */
  @NotNull private String name;

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
   */
  public GuidoGroup(
      @NotNull String id,
      int weight,
      @NotNull String name,
      @NotNull GuidoValuesMap preferences,
      @NotNull Set<PermissionStack> permissions) {
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
    this.name = name;
    this.weight = weight;
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
    this(Guido.getDataLoader().nextGroupId(), weight, name, preferences, permissions);
  }

  /** @deprecated this may only be used be used by json */
  public GuidoGroup() {
    this("", 1000, "", new GuidoValuesMap(), new HashSet<>());
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public void setName(@NotNull String name) {
    this.name = name;
  }

  @Override
  public void onRemove() {
    new GroupUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Time getToRemove() {
    return new Time(5, Unit.MINUTES);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GuidoGroup)) return false;

    GuidoGroup that = (GuidoGroup) o;

    if (!this.id.equals(that.id)) return false;
    return this.name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NotNull GuidoGroup cache() {
    return (GuidoGroup) BotGroup.super.cache();
  }
}
