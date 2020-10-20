package com.starfishst.bot.handlers.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGroup;
import com.starfishst.bot.api.events.data.group.GroupUnloadedEvent;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An implementation for the bot group TODO create permissions for groups */
public class GuidoGroup extends Catchable implements BotGroup {

  /** The id of the group */
  @NotNull private final String id;

  /** The preferences of the group */
  @NotNull private final GuidoValuesMap preferences;

  /** The permissions of the group */
  @NotNull private final Set<GuidoPermissionStack> permissions;

  /**
   * Create the group
   *
   * @param id the id of the group
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   * @param addToCache whether to add this group to cache
   */
  public GuidoGroup(
      @NotNull String id,
      @NotNull GuidoValuesMap preferences,
      @NotNull Set<GuidoPermissionStack> permissions,
      boolean addToCache) {
    super(Time.fromString("5m"), addToCache);
    this.id = id;
    this.preferences = preferences;
    this.permissions = permissions;
  }

  /**
   * Create the group
   *
   * @param preferences the preferences of the group
   * @param permissions the permissions of the group
   */
  public GuidoGroup(
      @NotNull GuidoValuesMap preferences, @NotNull Set<GuidoPermissionStack> permissions) {
    this(Guido.getDataLoader().nextGroupId(), preferences, permissions, true);
  }

  /** @deprecated this may only be used be used by json */
  public GuidoGroup() {
    this("", new GuidoValuesMap(), new HashSet<>(), false);
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new GroupUnloadedEvent(this).call();
  }

  @Override
  public @NotNull Set<GuidoPermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NotNull String getId() {
    return this.id;
  }

  @NotNull
  @Override
  public GuidoValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public @NotNull GuidoGroup refresh() {
    return (GuidoGroup) super.refresh();
  }
}
