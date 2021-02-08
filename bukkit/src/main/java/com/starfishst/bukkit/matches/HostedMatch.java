package com.starfishst.bukkit.matches;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Informative;
import me.googas.api.matches.AbstractMatch;

public class HostedMatch implements Informative {

  /** This is the id that represents in {@link AbstractMatch} */
  @NonNull @Getter private final String id;

  /**
   * The list of participants that are playing in the match. This list should not be modified unless
   * it is really required
   */
  @NonNull @Getter private final Set<HostedPlayer> participants;

  /** The ladder which is being played */
  @Nullable @Getter private final String ladder;

  /** The details of the match */
  @NonNull @Getter private final Map<String, Map<String, Object>> information;

  public HostedMatch(
      @NonNull String id,
      @NonNull Set<HostedPlayer> participants,
      @Nullable String ladder,
      @NonNull Map<String, Map<String, Object>> information) {
    this.id = id;
    this.participants = participants;
    this.ladder = ladder;
    this.information = information;
  }
}
