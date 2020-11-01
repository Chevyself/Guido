package me.googas.api.client.data;

import me.googas.api.permissions.Permission;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission */
public class PermissionImpl implements Permission {

  /** The node of the permission */
  @NotNull private final String node;

  /** Whether the permission is enabled */
  private final boolean enabled;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   */
  public PermissionImpl(@NotNull String node, boolean enabled) {
    this.node = node;
    this.enabled = enabled;
  }

  @Override
  public @NotNull String getNode() {
    return this.node;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String toString() {
    return "Permission{" + "node='" + this.node + '\'' + ", enabled=" + this.enabled + '}';
  }
}
