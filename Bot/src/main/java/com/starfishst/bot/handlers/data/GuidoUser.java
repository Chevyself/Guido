package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.data.user.BotUserLangUpdatedEvent;
import com.starfishst.bot.api.events.data.user.BotUserLoadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.ValuesMap;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements BotUser {

  /** The unique id of the discord user */
  private final long id;
  /** The permissions that the user has */
  @NotNull private final Set<PermissionStack> permissions;

  /** The preferences of the user */
  @NotNull private final GuidoValuesMap preferences;

  /**
   * Create the guido user
   *
   * @param id the discord id
   * @param permissions the permissions of the user
   * @param preferences the preferences of the user
   */
  public GuidoUser(
      long id, @NotNull Set<PermissionStack> permissions, @NotNull GuidoValuesMap preferences) {
    super(Time.fromString("3m"));
    this.id = id;
    this.permissions = permissions;
    this.preferences = preferences;
    new BotUserLoadedEvent(this).call();
  }

  /** Create the guido user. This is deprecated because only gson may use it */
  @Deprecated
  public GuidoUser() {
    this(0, new HashSet<>(), new GuidoValuesMap());
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    new BotUserUnloadedEvent(this).call();
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public @NotNull ValuesMap getPreferences() {
    return this.preferences;
  }

  @Override
  public void setLang(@NotNull String lang) {
    if (!new BotUserLangUpdatedEvent(this, lang).callAndGet()) {
      BotUser.super.setLang(lang);
    }
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }
}
