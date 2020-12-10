package me.googas.bot.core.types.permissions;

import lombok.NonNull;
import me.googas.api.permissions.Permission;

/** An implementation for guido permissions */
public class GuidoPermission implements Permission {

  /** The node of the permission */
  @NonNull private final String node;

  /** Whether the permission is enabled */
  private final boolean enabled;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   */
  public GuidoPermission(@NonNull String node, boolean enabled) {
    this.node = node;
    this.enabled = enabled;
  }

  /**
   * Get the node of the permission. This node is the representation of the permission as a string
   *
   * @return the node if the permission
   */
  @Override
  public @NonNull String getNode() {
    return this.node;
  }

  /**
   * Get if the permission is enabled. If the permission is enabled it means that the member is
   * allowed to use it
   *
   * @return whether the permission is enabled
   */
  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String toString() {
    return "GuidoPermission{" + "node='" + this.node + '\'' + ", enabled=" + this.enabled + '}';
  }
}
