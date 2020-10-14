package com.starfishst.bot.api.data;

import com.starfishst.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import com.starfishst.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.guido.api.data.Permissible;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/** A bot implementation for {@link Permissible} */
public interface BotPermissible extends Permissible<GuidoPermission> {

  /**
   * Adds a permission for this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @param enabled whether the permission is enabled
   */
  default void addPermission(@NotNull String context, @NotNull String node, boolean enabled) {
    PermissionStack<GuidoPermission> stack = this.getPermissions(context);
    if (stack == null) {
      stack = new GuidoPermissionStack(context, new HashSet<>());
      this.getPermissions().add(stack);
    }
    GuidoPermission permission = new GuidoPermission(node, enabled);
    stack.add(permission);
    new PermissiblePermissionAddedEvent(this, stack, permission);
  }

  /**
   * Removes the permission from this permissible
   *
   * @param context the context of the permission
   * @param node the node of the permission
   * @return whether the permission was removed. True if it was removed false otherwise
   */
  default boolean removePermission(@NotNull String context, @NotNull String node) {
    PermissionStack<GuidoPermission> stack = this.getPermissions(context);
    if (stack != null) {
      if (stack.hasPermission(node)) {
        stack.getPermissions().removeIf(permission -> permission.getNode().equalsIgnoreCase(node));
        new PermissiblePermissionRemovedEvent(this, node);
        return true;
      }
    }
    return false;
  }
}
