package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotRole;
import com.starfishst.bot.api.events.data.role.BotRoleLoadedEvent;
import com.starfishst.bot.api.events.data.role.BotRoleUnloadedEvent;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/**
 * This object represents the data for a role. Roles can be permissible which makes them have their
 * own data
 */
public class GuidoRole extends Catchable implements BotRole {

  /** The unique id of the role */
  private final long id;

  /** The unique id of the guild where this role exists */
  private final long guildId;

  /** The set of permission that the role possess */
  @NotNull private final Set<PermissionStack> permissions;

  /**
   * Create the guido role
   *
   * @param id the id of the role
   * @param guildId the unique id of the guild where this role exists
   * @param permissions the permission that the role posses
   */
  public GuidoRole(long id, long guildId, @NotNull Set<PermissionStack> permissions) {
    super(Time.fromString("3m"));
    this.id = id;
    this.guildId = guildId;
    this.permissions = permissions;
    new BotRoleLoadedEvent(this).call();
  }

  /** Create the guido role. Deprecated because this constructor may only be used by GSON */
  @Deprecated
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
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotRoleUnloadedEvent(this).call();
  }
}
