package me.googas.bot;

import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.handlers.ranks.RanksProvider;
import me.googas.server.RankRange;

public class GuidoRanksProvider implements RanksProvider {

  @NonNull private final GuidoBotRuntime runtime;

  public GuidoRanksProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull ImmutableCollection<? extends RankRange> getRanks() {
    return this.runtime.getBotJda().getGuidoGuild().getRanges();
  }
}
