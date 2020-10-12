package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.data.user.BotUserLoadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements BotUser {

  /** The unique id of the user */
  private final String id;
  /** The permissions that the user has */
  @NotNull private final Set<PermissionStack> permissions;

  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;

  /**
   * Create the guido user
   *
   * @param id the user id
   * @param permissions the permissions of the user
   * @param preferences the preferences of the user
   */
  public GuidoUser(
      @NotNull String id,
      @NotNull Set<PermissionStack> permissions,
      @NotNull GuidoValuesMap preferences) {
    super(Time.fromString("5m"));
    this.id = id;
    this.permissions = permissions;
    this.preferences = preferences;
    new BotUserLoadedEvent(this).call();
  }

  /** Create the guido user. This is deprecated because only gson may use it */
  @Deprecated
  public GuidoUser() {
    this("-1", new HashSet<>(), new GuidoValuesMap());
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  @NotNull
  public String getId() {
    return id;
  }
}
