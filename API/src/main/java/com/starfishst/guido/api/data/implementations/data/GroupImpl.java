package com.starfishst.guido.api.data.implementations.data;

import com.starfishst.guido.api.data.Group;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/** An implementation for group */
public class GroupImpl extends Catchable implements Group<PermissionImpl, PermissionStackImpl> {

  /** The id of the group */
  @NotNull private final String id;

  /** The name of the group */
  @NotNull private final String name;

  /** The preferences of the group */
  @NotNull private final ValuesMapImpl preferences;

  /** The permissions of the group */
  @NotNull private final Set<PermissionStackImpl> permissions;

  /**
   * Create the group
   *
   * @param id the id of the group
   * @param name the name of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   */
  public GroupImpl(
      @NotNull String id,
      @NotNull String name,
      @NotNull ValuesMapImpl preferences,
      @NotNull Set<PermissionStackImpl> permissions) {
    this.id = id;
    this.name = name;
    this.preferences = preferences;
    this.permissions = permissions;
  }

  /** @deprecated this may only be used be used by json */
  public GroupImpl() {
    this("", "", new ValuesMapImpl(), new HashSet<>());
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {}

  @Override
  public @NotNull Set<PermissionStackImpl> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NotNull String getId() {
    return this.id;
  }

  @Override
  public @NotNull String getName() {
    return null;
  }

  @NotNull
  @Override
  public ValuesMapImpl getPreferences() {
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
