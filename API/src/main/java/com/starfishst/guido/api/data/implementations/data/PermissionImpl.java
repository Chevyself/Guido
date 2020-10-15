package com.starfishst.guido.api.data.implementations.data;

import com.starfishst.guido.api.data.Permission;
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
}
