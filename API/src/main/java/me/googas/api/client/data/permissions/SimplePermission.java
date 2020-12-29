package me.googas.api.client.data.permissions;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.permissions.Permission;

public class SimplePermission implements Permission {

  @NonNull private final String node;
  private final boolean enabled;
  private final long expires;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @param expires the millis of when the permission expires
   */
  public SimplePermission(@NonNull String node, boolean enabled, long expires) {
    this.node = node;
    this.enabled = enabled;
    this.expires = expires;
  }

  /** @deprecated this constructor may only be used by gson */
  public SimplePermission() {
    this("", false, 0);
  }

  @Override
  public @NonNull String getNode() {
    return this.node;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public long expires() {
    return this.expires;
  }

  @Override
  public boolean setExpires(long expires) {
    return false;
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
