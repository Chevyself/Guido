package me.googas.api.matches.ladder;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;

public interface LadderProvider {

  @NonNull
  Optional<? extends Ladder> getByName(@NonNull String name);;;;

  @NonNull
  ImmutableCollection<? extends Ladder> getLadders();
}
