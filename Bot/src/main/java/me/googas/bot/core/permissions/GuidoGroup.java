package me.googas.bot.core.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.server.receptors.GroupReceptors;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.group.GroupUnloadedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import me.googas.bot.api.types.BotCatchable;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for the bot group */
public class GuidoGroup implements Group, BotCatchable {

  @NonNull
  public static GroupReceptors.GroupSupplier SUPPLIER =
      (weight, preferences, permissions, name, parents) ->
          new GuidoGroup(weight, new GuidoValuesMap(preferences), permissions, name, parents)
              .cache();

  @NonNull private final String id;
  private int weight;
  @NonNull private final GuidoValuesMap preferences;
  @NonNull private final Set<PermissionStack> permissions;
  @NonNull private final List<String> parents;
  @NonNull private String name;

  /**
   * Create the group
   *
   * @param id the id of the group
   * @param weight the weight of the group
   * @param name the name of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   * @param parents the ids of the parents of the group
   */
  public GuidoGroup(
      @NonNull String id,
      int weight,
      @NonNull String name,
      @NonNull GuidoValuesMap preferences,
      @NonNull Set<PermissionStack> permissions,
      @NonNull List<String> parents) {
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
    this.name = name;
    this.weight = weight;
    this.parents = parents;
  }

  /**
   * Create the group
   *
   * @param weight the weight of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   * @param name the name of the group
   * @param parents the ids of the parents of the group
   */
  public GuidoGroup(
      int weight,
      @NonNull GuidoValuesMap preferences,
      @NonNull Set<PermissionStack> permissions,
      String name,
      List<String> parents) {
    this(
        Guido.getHandlers().getLoader().getGroups().nextGroupId(),
        weight,
        name,
        preferences,
        permissions,
        parents);
  }

  /** @deprecated this may only be used be used by json */
  public GuidoGroup() {
    this("", 1000, "", new GuidoValuesMap(), new HashSet<>(), new ArrayList<>());
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public void setName(@NonNull String name) {
    this.name = name;
  }

  @Override
  public void onRemove() {
    new GroupUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(5, Unit.MINUTES);
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NonNull String getId() {
    return this.id;
  }

  @Override
  public int getWeight() {
    return this.weight;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @NonNull
  @Override
  public GuidoValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NonNull Collection<String> getParents() {
    return this.parents;
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

  @Override
  public @NonNull GuidoGroup cache() {
    return (GuidoGroup) BotCatchable.super.cache();
  }

  @Override
  public boolean addPermission(
      @NonNull String context, @NonNull String node, boolean enabled, long expires) {
    boolean added = Group.super.addPermission(context, node, enabled, expires);
    if (added) new PermissiblePermissionAddedEvent(this, context, node, enabled, expires).call();
    return added;
  }

  @Override
  public boolean removePermission(@NonNull String context, @NonNull String node) {
    boolean removed = Group.super.removePermission(context, node);
    if (removed) new PermissiblePermissionRemovedEvent(this, context, node).call();
    return removed;
  }
}
