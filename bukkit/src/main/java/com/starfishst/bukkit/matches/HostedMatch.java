package com.starfishst.bukkit.matches;

import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;

public class HostedMatch {

  /** This is the id that represents in {@link me.googas.api.matches.minecraft.MinecraftMatch} */
  @NonNull @Getter private final UUID id;

  /**
   * The list of participants that are playing in the match. This list should not be modified unless
   * it is really required
   */
  @NonNull @Getter private final Set<HostedPlayer> participants;

  /** The ladder which is being played */
  @Getter private final String ladder;

  public HostedMatch(@NonNull UUID id, @NonNull Set<HostedPlayer> participants, String ladder) {
    this.id = id;
    this.participants = participants;
    this.ladder = ladder;
  }
}
