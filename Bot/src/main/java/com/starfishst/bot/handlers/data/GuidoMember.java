package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.api.events.data.BotMemberLoadedEvent;
import com.starfishst.bot.api.events.data.BotMemberUnloadedEvent;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * The data for a member. This class represents all the data for certain discord user that is inside
 * a guild. This is called a member
 */
public class GuidoMember extends Catchable implements BotMember {

  /** The unique id that represents the member */
  private final long id;

  /** The unique id where this member is member on */
  private final long guildId;

  /** The permissions that the member posses */
  @NotNull private final Set<PermissionStack> permissions;

  /** The stats of the member */
  @NotNull private final HashMap<String, Double> stats;

  /**
   * Create the guido member
   *
   * @param id the id of the member
   * @param guildId the guild where this member is member in
   * @param permissions the permissions that the member posses
   * @param stats the stats of the member
   */
  public GuidoMember(
      long id,
      long guildId,
      @NotNull Set<PermissionStack> permissions,
      @NotNull HashMap<String, Double> stats) {
    super(Time.fromString("3m"));
    this.id = id;
    this.guildId = guildId;
    this.permissions = permissions;
    this.stats = stats;
    new BotMemberLoadedEvent(this).call();
  }

  /** Create the guido member. This is deprecated because only GSON may use it */
  @Deprecated
  public GuidoMember() {
    this(0, 0, new HashSet<>(), new HashMap<>());
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NotNull HashMap<String, Double> getStats() {
    return this.stats;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {
    new BotMemberUnloadedEvent(this).call();
  }
}
