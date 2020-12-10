package me.googas.api.client.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** An implementation for group */
public class SimpleGroup implements Group {

  /** The id of the group */
  @NonNull private final String id;

  /** The weight of the group */
  private final int weight;

  /** The name of the group */
  @NonNull private final String name;

  /** The preferences of the group */
  @NonNull private final ValuesMap preferences;

  /** The permissions of the group */
  @NonNull private final Set<PermissionStack> permissions;

  /** The ids of the parents of the group */
  @NonNull private final List<String> parents;

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
  public SimpleGroup(
      @NonNull String id,
      int weight,
      @NonNull String name,
      @NonNull ValuesMap preferences,
      @NonNull Set<PermissionStack> permissions,
      @NonNull List<String> parents) {
    this.id = id;
    this.weight = weight;
    this.name = name;
    this.preferences = preferences;
    this.permissions = permissions;
    this.parents = parents;
  }

  /** @deprecated this may only be used be used by json */
  public SimpleGroup() {
    this("", 1000, "", new SimpleValuesMap(), new HashSet<>(), new ArrayList<>());
  }

  @Override
  public void onRemove() {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(0, Unit.SECONDS);
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public boolean addPermission(@NonNull String context, @NonNull String node, boolean enabled) {
    return false;
  }

  @Override
  public boolean removePermission(@NonNull String context, @NonNull String node) {
    return false;
  }

  @Override
  public void setWeight(int weight) {
    throw new UnsupportedOperationException("Cannot set the weight of implementation groups");
  }

  @Override
  public void setName(@NonNull String name) {
    throw new UnsupportedOperationException("Cannot change the name of implemented groups");
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
  public ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NonNull Collection<String> getParents() {
    return this.parents;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("weight", this.weight)
        .append("name", this.name)
        .append("preferences", this.preferences)
        .append("permissions", this.permissions)
        .append("parents", this.parents)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof SimpleGroup)) return false;
    SimpleGroup that = (SimpleGroup) object;
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
