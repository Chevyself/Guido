package com.starfishst.guido.api.data.implementations.data;

import com.starfishst.guido.api.data.PermissionStack;
import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission stack */
public class PermissionStackImpl implements PermissionStack<PermissionImpl> {

  /** The context of the stack */
  @NotNull private final String context;

  /** The permissions of the stack */
  @NotNull private final Set<PermissionImpl> permissions;

  /**
   * Create the stack
   *
   * @param context the context of the stack
   * @param permissions the permissions of the stack
   */
  public PermissionStackImpl(@NotNull String context, @NotNull Set<PermissionImpl> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  @Override
  public @NotNull String getContext() {
    return this.context;
  }

  @Override
  public @NotNull Collection<PermissionImpl> getPermissions() {
    return this.permissions;
  }

  @Override
  public String toString() {
    return "PermissionStackImpl{" +
            "context='" + this.context + '\'' +
            ", permissions=" + this.permissions +
            '}';
  }
}
