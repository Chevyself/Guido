package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.api.events.data.user.BotUserLangUpdatedEvent;
import com.starfishst.bot.api.events.data.user.BotUserLoadedEvent;
import com.starfishst.bot.api.events.data.user.BotUserUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements BotUser {

  /** The unique id of the discord user */
  private final long id;
  /** The permissions that the user has */
  @NotNull private final Set<PermissionStack> permissions;
  /** The language that the user is using */
  @NotNull private String lang;

  /**
   * Create the guido user
   *
   * @param id the discord id
   * @param lang the language that the user is using
   * @param permissions the permissions of the user
   */
  public GuidoUser(long id, @NotNull String lang, @NotNull Set<PermissionStack> permissions) {
    super(Time.fromString("3m"));
    this.id = id;
    this.lang = lang;
    this.permissions = permissions;
    new BotUserLoadedEvent(this).call();
  }

  /** Create the guido user. This is deprecated because only gson may use it */
  @Deprecated
  public GuidoUser() {
    this(0, "en", new HashSet<>());
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
  public @NotNull String getLang() {
    return this.lang;
  }

  @Override
  public void setLang(@NotNull String lang) {
    if (!new BotUserLangUpdatedEvent(this, lang).callAndGet()) {
      this.lang = lang;
    }
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }
}
