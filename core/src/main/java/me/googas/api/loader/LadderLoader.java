package me.googas.api.loader;

import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;

public interface LadderLoader extends DataLoader {
  Ladder getLadder(@NonNull String name);
}
