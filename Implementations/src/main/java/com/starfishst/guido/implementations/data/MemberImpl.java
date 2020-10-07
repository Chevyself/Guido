package com.starfishst.guido.implementations.data;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.PermissionStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for members */
public class MemberImpl extends Catchable implements MemberData {

  /** The unique id that represents the member */
  private final long id;

  /** The unique id where this member is member on */
  private final long guildId;

  /** The permissions that the member posses */
  @NotNull private final Set<PermissionStack> permissions;

  /** The stats of the member */
  @NotNull private final HashMap<String, Double> stats;

  /** the links that are associated with this member */
  @NotNull private final HashMap<String, String> links;

  /**
   * Create the implementation member. This object is not added to cache automatically
   *
   * @param id the id of the member
   * @param guildId the guild where this member is member in
   * @param permissions the permissions that the member posses
   * @param stats the stats of the member
   * @param links the links that are associated with this member
   */
  public MemberImpl(
      long id,
      long guildId,
      @NotNull Set<PermissionStack> permissions,
      @NotNull HashMap<String, Double> stats,
      @NotNull HashMap<String, String> links) {
    super(Time.fromString("5m"));
    this.unload(false);
    this.id = id;
    this.guildId = guildId;
    this.permissions = permissions;
    this.stats = stats;
    this.links = links;
  }

  /** Create the member. This constructor may only be used by json */
  @Deprecated
  public MemberImpl() {
    this(-1, -1, new HashSet<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public @NotNull HashMap<String, String> getLinks() {
    return this.links;
  }

  @Override
  public @NotNull MemberImpl refresh() {
    return (MemberImpl) super.refresh();
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {}

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public long getGuildId() {
    return this.guildId;
  }

  @Override
  public @NotNull HashMap<String, Double> getStats() {
    return this.stats;
  }

  @Override
  public String toString() {
    return "MemberImpl{"
        + "id="
        + id
        + ", guildId="
        + guildId
        + ", permissions="
        + permissions
        + ", stats="
        + stats
        + ", links="
        + links
        + "} "
        + super.toString();
  }
}
