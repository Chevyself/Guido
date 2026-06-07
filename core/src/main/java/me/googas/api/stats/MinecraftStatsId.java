package me.googas.api.stats;

import java.util.UUID;
import lombok.NonNull;

public interface MinecraftStatsId extends StatsId {
  @NonNull
  UUID getLinkableId();
}
