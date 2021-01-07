package me.googas.bot.core.discord;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionAddedEvent;
import me.googas.bot.api.events.data.permissible.PermissiblePermissionRemovedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/**
 * This object represents the data for a role. Roles can be permissible which makes them have their
 * own data
 */
public class GuidoRole implements BotRole {

  private final long id;
  @NonNull private final Set<PermissionStack> permissions;

  /**
   * Create the guido role
   *
   * @param id the id of the role
   * @param permissions the permission that the role posses
   */
  public GuidoRole(long id, @NonNull Set<PermissionStack> permissions) {
    this.id = id;
    this.permissions = new HashSet<>(permissions);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoRole() {
    this(0, new HashSet<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NonNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onRemove() {
    new BotRoleUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull GuidoRole cache() {
    return (GuidoRole) BotRole.super.cache();
  }

  @Override
  public boolean addPermission(
      @NonNull String context, @NonNull String node, boolean enabled, long expires) {
    boolean added = BotRole.super.addPermission(context, node, enabled, expires);
    if (added) new PermissiblePermissionAddedEvent(this, context, node, enabled, expires).call();
    return added;
  }

  @Override
  public boolean removePermission(@NonNull String context, @NonNull String node) {
    boolean removed = BotRole.super.removePermission(context, node);
    if (removed) new PermissiblePermissionRemovedEvent(this, context, node).call();
    return removed;
  }
}
