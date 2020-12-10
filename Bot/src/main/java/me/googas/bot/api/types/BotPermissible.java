package me.googas.bot.api.types;

import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.permissions.Permissible;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import me.googas.bot.core.types.permissions.GuidoPermission;
import me.googas.bot.core.types.permissions.GuidoPermissionStack;

/** A bot implementation for {@link Permissible} */
public interface BotPermissible extends Permissible, BotCatchable {

  @Override
  default boolean addPermission(@NonNull String context, @NonNull String node, boolean enabled) {
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
  default boolean removePermission(@NonNull String context, @NonNull String node) {
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
