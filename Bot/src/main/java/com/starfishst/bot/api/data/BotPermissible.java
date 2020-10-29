package com.starfishst.bot.api.data;

import com.starfishst.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import com.starfishst.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import com.starfishst.bot.handlers.data.types.GuidoPermission;
import com.starfishst.bot.handlers.data.types.GuidoPermissionStack;
import me.googas.api.PermissionStack;
import java.util.HashSet;
import org.jetbrains.annotations.NotNull;

/** A bot implementation for {@link Permissible} */
public interface BotPermissible extends Permissible {

  @Override
  default boolean addPermission(@NotNull String context, @NotNull String node, boolean enabled) {
    PermissionStack stack = this.getPermissions(context);
    if (stack == null) {
      stack = new GuidoPermissionStack(context, new HashSet<>());
      this.getPermissions().add(stack);
    }
    if (!stack.containsPermission(node)) {
      GuidoPermission permission = new GuidoPermission(node, enabled);
      if (stack.add(permission)) {
        new PermissiblePermissionAddedEvent(this, stack, permission);
        return true;
      }
    }
    return false;
  }

  @Override
  default boolean removePermission(@NotNull String context, @NotNull String node) {
    PermissionStack stack = this.getPermissions(context);
    if (stack != null) {
      if (stack.containsPermission(node)) {
        stack.getPermissions().removeIf(permission -> permission.getNode().equalsIgnoreCase(node));
        new PermissiblePermissionRemovedEvent(this, context, node);
        return true;
      }
    }
    return false;
  }
}
