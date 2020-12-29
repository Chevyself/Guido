package me.googas.bot.core.permissions;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.permissions.Permission;
import me.googas.commons.builder.ToStringBuilder;

/** An implementation for guido permissions */
public class GuidoPermission implements Permission {

  @NonNull private final String node;
  private final boolean enabled;
  private final long expires;

  /**
   * Create the permission
   *
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   * @param expires when does the permission expires
   */
  public GuidoPermission(@NonNull String node, boolean enabled, long expires) {
    this.node = node;
    this.enabled = enabled;
    this.expires = expires;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoPermission() {
    this("", false, 0);
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
  public @NonNull String getNode() {
    return this.node;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("node", this.node)
        .append("enabled", this.enabled)
        .append("expires", this.expires)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoPermission that = (GuidoPermission) o;
    return this.enabled == that.enabled
        && this.expires == that.expires
        && this.node.equals(that.node);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.node, this.enabled, this.expires);
  }
}
