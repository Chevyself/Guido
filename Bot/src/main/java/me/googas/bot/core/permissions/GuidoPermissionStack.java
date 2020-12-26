package me.googas.bot.core.permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.builder.ToStringBuilder;

/** An implementation for permission stacks */
public class GuidoPermissionStack implements PermissionStack {

  @NonNull private final String context;
  @NonNull private final Set<Permission> permissions;

  /**
   * Create the permission stack
   *
   * @param context the context of the stack
   * @param permissions the permissions inside the stack
   */
  public GuidoPermissionStack(@NonNull String context, @NonNull Set<Permission> permissions) {
    this.context = context;
    this.permissions = permissions;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoPermissionStack() {
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoPermissionStack that = (GuidoPermissionStack) o;
    return Objects.equals(this.context, that.context)
        && Objects.equals(this.permissions, that.permissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.context, this.permissions);
  }
}
