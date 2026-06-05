package me.googas.api.matches.ladder;

import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;

public interface LadderProvider {

  @NonNull
  Optional<Ladder> getByName(@NonNull String name);;;;

  @NonNull
  Collection<Ladder> getLadders();
}
