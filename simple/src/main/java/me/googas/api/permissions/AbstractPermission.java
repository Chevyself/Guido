package me.googas.api.permissions;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Expirable;

/** This object represents a permission. A permission contains a node and whether it is enabled */
public class AbstractPermission implements Expirable {

  @NonNull @Getter private final String node;
  @Getter private final boolean enabled;
  @Getter private long expires;

  /**
   * Create the permission
   *
   * @param node the node string that represents the permission
   * @param enabled whether the permission is enabled
   * @param expires the date when the permission expires in millis
   */
  public AbstractPermission(@NonNull String node, boolean enabled, long expires) {
    this.node = node;
    this.enabled = enabled;
    this.expires = expires;
  }

  /** @deprecated this constructor may only be used by gson */
  public AbstractPermission() {
    this("", false, 0);
  }

  /**
   * Get node with the {@link #isEnabled()} appended. This will getId '-' + the node if {@link
   * #isEnabled()} happens to be false
   *
   * @return the node with the string appended
   */
  @NonNull
  public String getNodeAppended() {
    return this.isEnabled() ? this.getNode() : "-" + this.getNode();
  }

  @Override
  public boolean setExpires(long expires) {
    this.expires = expires;
    return true;
  }
}
