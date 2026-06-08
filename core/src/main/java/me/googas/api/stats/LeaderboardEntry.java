package me.googas.api.stats;

import lombok.NonNull;

public interface LeaderboardEntry {
  @NonNull
  String getDisplay();

  double getValue();

  double getWins();

  double getLoses();
}
