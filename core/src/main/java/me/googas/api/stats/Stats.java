package me.googas.api.stats;

import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.ImmutableMap;

public interface Stats {
  @NonNull String EMPTY_CONTEXT = "no-context";
  @NonNull String LADDER_ELO_SUFFIX = "-elo";
  @NonNull String LADDER_WINS_SUFFIX = "-wins";
  @NonNull String LADDER_LOSES_SUFFIX = "-loses";

  @NonNull
  StatsId getId();

  double getElo(@NonNull Ladder ladder, @NonNull ImmutableCollection<? extends Ladder> ladders);

  double getElo(@NonNull Ladder ladder);

  double getWins(@NonNull Ladder ladder);

  double getLoses(@NonNull Ladder ladder);

  double getStat(@NonNull String key);

  @NonNull
  ImmutableMap<String, Double> getMap();

  void increaseElo(@NonNull Ladder ladder, float winnersDifference);

  void increaseWins(@NonNull Ladder ladder, float value);

  void decreaseElo(@NonNull Ladder ladder, float losersDifference);

  void increaseLoses(@NonNull Ladder ladder, int value);

  void increasePlayed(@NonNull Ladder ladder, int value);
}
