package me.googas.bot.core.loader.types;

import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.stats.MinecraftStatsId;
import me.googas.api.stats.StatsLinkableType;

public class GenericStatsId implements MinecraftStatsId {

  @NonNull @Getter private final String context;
  @NonNull @Getter private final UUID linkableId;

  public GenericStatsId(@NonNull String context, @NonNull UUID linkableId) {
    this.context = context;
    this.linkableId = linkableId;
  }

  @Override
  public @NonNull StatsLinkableType getLinkableType() {
    return StatsLinkableType.MINECRAFT;
  }
}
