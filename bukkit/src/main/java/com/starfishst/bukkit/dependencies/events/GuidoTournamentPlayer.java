package com.starfishst.bukkit.dependencies.events;

import dev.pgm.events.team.TournamentPlayer;
import java.util.UUID;
import lombok.NonNull;

public class GuidoTournamentPlayer implements TournamentPlayer {

  @NonNull private final UUID uuid;

  private final boolean canVeto;

  public GuidoTournamentPlayer(@NonNull UUID uuid, boolean canVeto) {
    this.uuid = uuid;
    this.canVeto = canVeto;
  }

  @Override
  public UUID getUUID() {
    return this.uuid;
  }

  @Override
  public boolean canVeto() {
    return this.canVeto;
  }
}
