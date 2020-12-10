package me.googas.bot.api.events.data.permissible;

import lombok.NonNull;
import me.googas.bot.api.types.BotPermissible;

/** Called when a permissible gets a permission removed */
public class PermissiblePermissionRemovedEvent extends PermissibleEvent {

  /** The node of the permission that was removed */
  @NonNull private final String node;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param context the context where the permission was removed
   * @param node the node of the permission that was removed
   */
  public PermissiblePermissionRemovedEvent(
      @NonNull BotPermissible permissible, @NonNull String context, @NonNull String node) {
    super(permissible);
    this.node = node;
  }

  @Override
  public String toString() {
    return "PermissiblePermissionRemovedEvent{"
        + "node='"
        + this.node
        + '\''
        + "} "
        + super.toString();
  }
}
