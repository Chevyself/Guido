package com.starfishst.bot.api.events.data.permissible;

import com.starfishst.bot.api.data.BotPermissible;
import org.jetbrains.annotations.NotNull;

/** Called when a permissible gets a permission removed */
public class PermissiblePermissionRemovedEvent extends PermissibleEvent {

  /** The node of the permission that was removed */
  @NotNull private final String node;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param context the context where the permission was removed
   * @param node the node of the permission that was removed
   */
  public PermissiblePermissionRemovedEvent(
      @NotNull BotPermissible permissible, @NotNull String context, @NotNull String node) {
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
