package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.data.loader.BotLinkedData;
import com.starfishst.bot.api.events.data.links.LinkedDataLoadedEvent;
import com.starfishst.bot.api.events.data.links.LinkedDataUnloadedEvent;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.ValuesMap;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The implementation of bot linked ata */
public class GuidoLinkedData extends Catchable implements BotLinkedData {

  /** The type of the linked data */
  @NotNull private final LinkedDataType type;
  /** The way to identify this data */
  @NotNull private final GuidoValuesMap identification;
  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;
  /** The stats of the data */
  @NotNull private final HashMap<String, Double> stats;
  /** The permissions */
  @NotNull private final Set<PermissionStack<GuidoPermission>> permissions;
  /** The linked user */
  @Nullable private BotUser user;

  public GuidoLinkedData(
      boolean addToCache,
      @NotNull LinkedDataType type,
      @Nullable BotUser user,
      @NotNull GuidoValuesMap identification,
      @NotNull GuidoValuesMap preferences,
      @NotNull HashMap<String, Double> stats,
      @NotNull Set<GuidoPermissionStack> permissions) {
    super(Time.fromString("3m"), addToCache);
    this.type = type;
    this.user = user;
    this.identification = identification;
    this.preferences = preferences;
    this.stats = stats;
    this.permissions = new HashSet<>(permissions);
  }

  /**
   * Get the set of permission stack. This are all the permissions that this entity posses
   *
   * @return the set of permissions of the entity
   */
  @Override
  public @NotNull Set<PermissionStack<GuidoPermission>> getPermissions() {
    return this.permissions;
  }

  @Override
  public void addToCache() {
    new LinkedDataLoadedEvent(this).call();
    super.addToCache();
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new LinkedDataUnloadedEvent(this).call();
  }

  /**
   * Get the stats of the entity
   *
   * @return the map of the stats
   */
  @Override
  public @NotNull HashMap<String, Double> getStats() {
    return this.stats;
  }

  /**
   * Get the type of linked data
   *
   * @return the type of linked data
   */
  @Override
  public @NotNull LinkedDataType getType() {
    return this.type;
  }

  /**
   * Get how this linked data is identified
   *
   * @return the identification of the data
   */
  @Override
  public @NotNull ValuesMap getIdentification() {
    return this.identification;
  }

  /**
   * Get the preferences of a linked data
   *
   * @return the preferences
   */
  @Override
  public @NotNull ValuesMap getPreferences() {
    return this.preferences;
  }

  /**
   * Get the user that is linked to this data
   *
   * @return the user that is linked to this data
   */
  @Override
  public @Nullable BotUser getLinkedUser() {
    return this.user;
  }

  @Override
  public void addPermission(@NotNull String context, @NotNull GuidoPermission permission) {
    PermissionStack<GuidoPermission> stack = this.getPermissions(context);
    if (stack == null) {
      stack = new GuidoPermissionStack(context, new HashSet<>());
      this.getPermissions().add(stack);
    }
    stack.add(permission);
  }

  @Override
  public void removePermission(@NotNull String context, @NotNull GuidoPermission permission) {
    PermissionStack<GuidoPermission> stack = this.getPermissions(context);
    if (stack != null) {
      stack.remove(permission);
    }
  }

  /**
   * Set the linked user to this data
   *
   * @param user the new linked user
   */
  @Override
  public void setLinkedUser(@Nullable UserData user) {
    if (user instanceof BotUser) {
      this.user = (BotUser) user;
    } else {
      throw new UnsupportedOperationException(user + " must be a " + BotUser.class);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof GuidoLinkedData)) return false;

    GuidoLinkedData that = (GuidoLinkedData) object;

    if (type != that.type) return false;
    return identification.equals(that.identification);
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + identification.hashCode();
    return result;
  }
}
