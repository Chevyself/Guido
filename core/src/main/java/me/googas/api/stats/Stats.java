package me.googas.api.stats;

import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;

public interface Stats {
  @NonNull static final String EMPTY_CONTEXT = "no-context";

  double getElo(@NonNull Ladder ladder, @NonNull Collection<Ladder> ladders);

  double getElo(@NonNull Ladder ladder);

  double getWins(@NonNull Ladder ladder);

  double getLoses(@NonNull Ladder ladder);

  double getStat(@NonNull String key);

  @NonNull
  Map<String, Double> getMap();
}
