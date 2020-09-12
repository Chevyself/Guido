package com.starfishst.guido.pgm.configuration;

import com.starfishst.guido.pgm.api.config.PermissionData;
import org.jetbrains.annotations.NotNull;

/** A permission implementation for guido */
public class GuidoPermission implements PermissionData {

  /** The node of the permission */
  @NotNull private final String node;

  /** Whether the permission is enabled */
  private final boolean enabled;

  /**
   * Create the guido permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enableed
   */
  public GuidoPermission(@NotNull String node, boolean enabled) {
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
