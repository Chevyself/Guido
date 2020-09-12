package com.starfishst.guido.pgm.api.config;

import org.jetbrains.annotations.NotNull;

/** The data of a permission */
public interface PermissionData {

  /**
   * The node of the permission
   *
   * @return the node of the string
   */
  @NotNull
  String getNode();

  /**
   * Whether the permission is enabled
   *
   * @return true if the permission is enabled
   */
  boolean isEnabled();
}
