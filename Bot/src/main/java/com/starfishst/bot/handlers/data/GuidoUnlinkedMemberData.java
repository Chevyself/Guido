package com.starfishst.bot.handlers.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUnlinkedMemberData;
import com.starfishst.bot.api.events.data.unlinked.BotUnlinkedMemberLoadedEvent;
import com.starfishst.bot.api.events.data.unlinked.BotUnlinkedMemberUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashMap;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for {@link BotUnlinkedMemberData} */
public class GuidoUnlinkedMemberData extends Catchable implements BotUnlinkedMemberData {

  /** The key of the link */
  @NotNull private final String key;

  /** The value of the link */
  @NotNull private final String value;

  /** The unique id where this member is member on */
  private final long guildId;

  /** The permissions that the member posses */
  @NotNull private final Set<PermissionStack> permissions;

  /** The stats of the member */
  @NotNull private final HashMap<String, Double> stats;

  /**
   * Create the unlinked member
   *
   * @param key the key of the link
   * @param value the value of the link
   * @param guildId the id where this member is from
   * @param permissions the permissions of the member
   * @param stats the stats of the member
   */
  public GuidoUnlinkedMemberData(
      @NotNull String key,
      @NotNull String value,
      long guildId,
      @NotNull Set<PermissionStack> permissions,
      @NotNull HashMap<String, Double> stats) {
    super(Time.fromString("30m"));
    this.key = key;
    this.value = value;
    this.guildId = guildId;
    this.permissions = permissions;
    this.stats = stats;
    new BotUnlinkedMemberLoadedEvent(this);
  }

  /**
   * Create a copy of a unlinked member. This will not add it to cache or call {@link
   * BotUnlinkedMemberLoadedEvent}
   *
   * @param member the member that is being copied
   */
  public GuidoUnlinkedMemberData(@NotNull GuidoUnlinkedMemberData member) {
    super(Time.fromString("0s"));
    this.unload(false);
    this.key = member.getKey();
    this.value = member.getValue();
    this.guildId = member.getGuildId();
    this.permissions = member.getPermissions();
    this.stats = member.getStats();
  }

  @NotNull
  @Override
  public GuidoUnlinkedMemberData copy() {
    return new GuidoUnlinkedMemberData(this);
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    new BotUnlinkedMemberUnloadedEvent(this);
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void delete() {
    this.unload(false);
    Guido.getDataLoader().deleteUnlinked(this);
  }

  @Override
  public @NotNull String getKey() {
    return this.key;
  }

  @Override
  public @NotNull String getValue() {
    return this.value;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public @NotNull HashMap<String, Double> getStats() {
    return this.stats;
  }
}
