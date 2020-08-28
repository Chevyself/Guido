package com.starfishst.guido.handlers.data;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.Permission;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.events.data.UserDataLangUpdatedEvent;
import com.starfishst.guido.api.events.data.UserDataLoadedEvent;
import com.starfishst.guido.api.events.data.UserDataUnloadedEvent;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An user that operates this bot */
public class GuidoUser extends Catchable implements UserData {

  /** The unique id of the discord user */
  private final long id;
  /** The permissions that the user has */
  @NotNull private final Set<Permission> permissions;
  /** The language that the user is using */
  @NotNull private String lang;

  /**
   * Create the guido user
   *
   * @param id the discord id
   * @param lang the language that the user is using
   * @param permissions the permissions of the user
   */
  public GuidoUser(long id, @NotNull String lang, @NotNull Set<Permission> permissions) {
    super(Time.fromString("3m"));
    this.id = id;
    this.lang = lang;
    this.permissions = permissions;
    new UserDataLoadedEvent(this).call();
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
    new UserDataUnloadedEvent(this).call();
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
    if (!new UserDataLangUpdatedEvent(this, lang).callAndGet()) {
      this.lang = lang;
    }
  }

  @Override
  public @NotNull Set<Permission> getPermissions() {
    return this.permissions;
  }
}
