package me.googas.bot;

import java.util.Optional;
import lombok.NonNull;
import me.googas.api.matches.ladder.LadderProvider;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.matches.ladder.PlayableLadder;

public class GuidoLadderProvider implements LadderProvider {
  @NonNull private final GuidoBotRuntime runtime;

  public GuidoLadderProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Optional<PlayableLadder> getByName(@NonNull String name) {
    return runtime.getBotJda().getGuidoGuild().getLadder(name);
  }

  @Override
  public @NonNull ImmutableCollection<? extends PlayableLadder> getLadders() {
    return runtime.getBotJda().getGuidoGuild().getLadders();
  }
}
