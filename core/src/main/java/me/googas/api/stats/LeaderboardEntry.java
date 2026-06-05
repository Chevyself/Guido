package me.googas.api.stats;

import lombok.NonNull;

public interface LeaderboardEntry extends Stats {
  @NonNull
  String getDisplay();
}
