package me.googas.bot.core.loader.jsongo;

import lombok.NonNull;
import me.googas.api.loader.LadderLoader;
import me.googas.api.matches.ladder.Ladder;

public class JsonLadderLoader extends SimpleJsongoLoader implements LadderLoader {

  public JsonLadderLoader(@NonNull JsongoLoader loader) {
    super(loader);
  }

  @Override
  public Ladder getLadder(@NonNull String name) {
    return null;
  }
}
