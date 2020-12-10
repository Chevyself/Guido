package me.googas.bot.core.types;

import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.types.BotRole;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/**
 * This object represents the data for a role. Roles can be permissible which makes them have their
 * own data
 */
public class GuidoRole implements BotRole {

  /** The unique id of the role */
  private final long id;

  /** The unique id of the guild where this role exists */
  private final long guildId;

  /** The set of permission that the role possess */
  @NonNull private final Set<PermissionStack> permissions;

  /**
   * Create the guido role
   *
   * @param id the id of the role
   * @param guildId the unique id of the guild where this role exists
   * @param permissions the permission that the role posses
   */
  public GuidoRole(long id, long guildId, @NonNull Set<PermissionStack> permissions) {
    this.id = id;
    this.guildId = guildId;
    this.permissions = new HashSet<>(permissions);
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoRole() {
    this(0, 0, new HashSet<>());
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
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

  /**
   * Adds this catchable in cache
   *
   * @return this same object instance
   */
  @Override
  public @NonNull GuidoRole cache() {
    return (GuidoRole) BotRole.super.cache();
  }
}
