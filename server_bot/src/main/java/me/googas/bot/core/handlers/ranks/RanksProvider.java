package me.googas.bot.core.handlers.ranks;

import java.util.Collection;
import lombok.NonNull;
import me.googas.bot.DiscordRankRange;

public interface RanksProvider {
  @NonNull
  Collection<DiscordRankRange> getRanks();
}
