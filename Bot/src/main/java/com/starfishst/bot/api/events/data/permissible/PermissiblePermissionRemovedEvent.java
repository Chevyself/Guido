package com.starfishst.bot.api.events.data.permissible;

import com.starfishst.guido.api.data.Permissible;
import org.jetbrains.annotations.NotNull;

/** Called when a permissible gets a permission removed */
public class PermissiblePermissionRemovedEvent extends PermissibleEvent {

  /** The node of the permission that was removed */
  @NotNull private final String node;

  /**
   * Create the event
   *
   * @param permissible the permissible involved in the event
   * @param node the node of the permission that was removed
   */
  public PermissiblePermissionRemovedEvent(@NotNull Permissible<?> permissible, @NotNull String node) {
    super(permissible);
    this.node = node;
  }
}
