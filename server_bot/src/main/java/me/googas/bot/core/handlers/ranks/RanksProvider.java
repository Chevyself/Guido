package me.googas.bot.core.handlers.ranks;

import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.DiscordRankRange;

public interface RanksProvider {
  @NonNull
  ImmutableCollection<DiscordRankRange> getRanks();
}
