package com.starfishst.guido.implementations.data;

import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.api.data.PermissionStack;
import com.starfishst.guido.api.data.UnlinkedMemberData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/** An implementation for an unlinked member */
public class UnlinkedMemberDataImpl extends Catchable implements UnlinkedMemberData {

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
   * Create the unlinked member. This object is not added to cache automatically
   *
   * @param key the key of the link
   * @param value the value of the link
   * @param guildId the guild of the member
   * @param permissions the permissions of the member
   * @param stats the stats of the member
   */
  public UnlinkedMemberDataImpl(
      @NotNull String key,
      @NotNull String value,
      long guildId,
      @NotNull Set<PermissionStack> permissions,
      @NotNull HashMap<String, Double> stats) {
    super(Time.fromString("5m"));
    this.unload(false);
    this.key = key;
    this.value = value;
    this.guildId = guildId;
    this.permissions = permissions;
    this.stats = stats;
  }

  /** This constructor may only be used by gson */
  @Deprecated
  public UnlinkedMemberDataImpl() {
    this("none", "none", -1, new HashSet<>(), new HashMap<>());
  }

  @Override
  public @NotNull Set<PermissionStack> getPermissions() {
    return this.permissions;
  }

  @Override
  public void onSecondsPassed() {}

  @Override
  public void onRemove() {}

  @Override
  public void delete() {
    throw new UnsupportedOperationException("Implementations cannot delete members");
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

  @Override
  public String toString() {
    return "UnlinkedMemberDataImpl{"
        + "key='"
        + key
        + '\''
        + ", value='"
        + value
        + '\''
        + ", guildId="
        + guildId
        + ", permissions="
        + permissions
        + ", stats="
        + stats
        + "} "
        + super.toString();
  }
}
