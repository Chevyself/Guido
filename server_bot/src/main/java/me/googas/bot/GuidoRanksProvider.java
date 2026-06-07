package me.googas.bot;

import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.core.handlers.ranks.RanksProvider;

public class GuidoRanksProvider implements RanksProvider {

  @NonNull private final GuidoBotRuntime runtime;

  public GuidoRanksProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull ImmutableCollection<DiscordRankRange> getRanks() {
    return this.runtime.getBotJda().getGuidoGuild().getRanges();
  }
}
