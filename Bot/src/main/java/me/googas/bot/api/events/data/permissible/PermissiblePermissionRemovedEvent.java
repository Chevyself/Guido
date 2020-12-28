package me.googas.bot.api.events.data.permissible;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.permissions.Permissible;
import me.googas.commons.builder.ToStringBuilder;

/** Called when a permissible gets a permission removed */
public class PermissiblePermissionRemovedEvent extends PermissibleEvent {

  @NonNull @Getter private final String context;
  @NonNull @Getter private final String node;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param context the context where the permission was removed
   * @param node the node of the permission that was removed
   */
  public PermissiblePermissionRemovedEvent(
      @NonNull Permissible permissible, @NonNull String context, @NonNull String node) {
    super(permissible);
    this.context = context;
    this.node = node;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("node", this.node).build();
  }
}
