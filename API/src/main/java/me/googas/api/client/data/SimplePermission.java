package me.googas.api.client.data;

import java.util.Objects;
import me.googas.api.permissions.Permission;
import org.jetbrains.annotations.NotNull;

/** An implementation for permission */
public class SimplePermission implements Permission {

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
  public SimplePermission(@NotNull String node, boolean enabled) {
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

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || this.getClass() != object.getClass()) return false;
    SimplePermission that = (SimplePermission) object;
    return this.enabled == that.enabled && this.node.equals(that.node);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.node, this.enabled);
  }
}
