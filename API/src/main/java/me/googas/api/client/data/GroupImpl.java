package me.googas.api.client.data;

import java.util.HashSet;
import java.util.Set;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** An implementation for group */
public class GroupImpl extends Catchable implements Group {

  /** The id of the group */
  @NotNull private final String id;

  /** The weight of the group */
  private final int weight;

  /** The name of the group */
  @NotNull private final String name;

  /** The preferences of the group */
  @NotNull private final ValuesMap preferences;

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
  public GroupImpl(
      @NotNull String id,
      int weight,
      @NotNull String name,
      @NotNull ValuesMap preferences,
      @NotNull Set<PermissionStack> permissions) {
    this.id = id;
    this.weight = weight;
    this.name = name;
    this.preferences = preferences;
    this.permissions = permissions;
  }

  /** @deprecated this may only be used be used by json */
  public GroupImpl() {
    this("", 1000, "", new ValuesMapImpl(), new HashSet<>());
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {}

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public boolean addPermission(@NotNull String context, @NotNull String node, boolean enabled) {
    return false;
  }

  @Override
  public boolean removePermission(@NotNull String context, @NotNull String node) {
    return false;
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
  public ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public String toString() {
    return "Group{"
        + "id='"
        + this.id
        + '\''
        + ", preferences="
        + this.preferences
        + ", permissions="
        + this.permissions
        + "} "
        + super.toString();
  }
}
