package me.googas.api.events.permissible;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.permissions.Permissible;

/** Called when a permissible gets a new permission */
public class PermissiblePermissionAddedEvent extends PermissibleEvent {

  @NonNull @Getter private final String context;
  @NonNull @Getter private final String node;
  @Getter private final boolean enabled;
  @Getter private final long expires;

  public PermissiblePermissionAddedEvent(
      @NonNull Permissible permissible,
      @NonNull String context,
      @NonNull String node,
      boolean enabled,
      long expires) {
    super(permissible);
    this.context = context;
    this.node = node;
    this.enabled = enabled;
    this.expires = expires;
  }

  @Override
  public String toString() {
    return "PermissiblePermissionAddedEvent{"
        + "context='"
        + context
        + '\''
        + ", node='"
        + node
        + '\''
        + ", enabled="
        + enabled
        + ", expires="
        + expires
        + '}';
  }
}
