package me.googas.api.loader;

import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.matches.ladder.Ladder;

public interface LadderLoader extends DataLoader {
  @Nullable
  Ladder getLadder(@NonNull String name);
}
