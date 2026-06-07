package me.googas.bot;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.ladder.LadderProvider;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.GuidoBotRuntime;

public class GuidoLadderProvider implements LadderProvider {
  @NonNull private final GuidoBotRuntime runtime;

  public GuidoLadderProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Optional<? extends Ladder> getByName(@NonNull String name) {
    return runtime.getBotJda().getGuidoGuild().getLadder(name);
  }

  @Override
  public @NonNull ImmutableCollection<? extends Ladder> getLadders() {
    return runtime.getBotJda().getGuidoGuild().getLadders();
  }
}
