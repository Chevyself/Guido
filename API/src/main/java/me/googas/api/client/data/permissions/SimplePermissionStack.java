package me.googas.api.client.data.permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.builder.ToStringBuilder;

/** An implementation for permission stack */
public class SimplePermissionStack implements PermissionStack {

  @NonNull private final String context;
  @NonNull private final Set<Permission> permissions;

  /**
   * Create the stack
   *
   * @param context the context of the stack
   * @param permissions the permissions of the stack
   */
  public SimplePermissionStack(@NonNull String context, @NonNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public SimplePermissionStack() {
    this("", new HashSet<>());
  }

  @Override
  public @NonNull String getContext() {
    return this.context;
  }

  @Override
  public @NonNull Collection<Permission> getPermissions() {
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
