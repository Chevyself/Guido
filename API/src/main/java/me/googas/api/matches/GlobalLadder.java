package me.googas.api.matches;

import me.googas.api.discord.GuildData;
import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;

/** The global ladder cannot be edited, this ladder calculates the global elo of the linked data */
public class GlobalLadder implements Ladder {

  /** A public static instance for global ladders */
  public static Ladder INSTANCE = new GlobalLadder();

  @Override
  public int playersPerTeam() {
    return 0;
  }

  @Override
  public int baseValue() {
    return 0;
  }

  @Override
  public @NotNull Queue createQueue(@NotNull GuildData data) {
    throw new UnsupportedOperationException("Global ladder cannot create queues");
  }

  @Override
  public @NotNull String getName() {
    return "global";
  }

  @Override
  public @NotNull ValuesMap getOptions() {
    throw new UnsupportedOperationException("Global ladder does not have options");
  }
}
