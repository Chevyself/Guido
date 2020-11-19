package me.googas.api.client.data;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission stack */
public class SimplePermissionStack implements PermissionStack {

  /** The context of the stack */
  @NotNull private final String context;

  /** The permissions of the stack */
  @NotNull private final Set<Permission> permissions;

  /**
   * Create the stack
   *
   * @param context the context of the stack
   * @param permissions the permissions of the stack
   */
  public SimplePermissionStack(@NotNull String context, @NotNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  @Override
  public @NotNull String getContext() {
    return this.context;
  }

  @Override
  public @NotNull Collection<Permission> getPermissions() {
    return this.permissions;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("context", this.context)
        .append("permissions", this.permissions)
        .build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimplePermissionStack that = (SimplePermissionStack) object;
    return this.context.equals(that.context);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.context);
  }
}
