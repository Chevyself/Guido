package me.googas.bot.core.handlers.ranks;

import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;
import me.googas.server.RankRange;

public interface RanksProvider {
  @NonNull
  ImmutableCollection<? extends RankRange> getRanks();
}
