package me.googas.api.matches;

import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.api.utility.ValuesMap;

/** The global ladder cannot be edited, this ladder calculates the global elo of the linked data */
public class GlobalLadder implements Ladder {

  /** A public static instance for global ladders */
  public static final Ladder INSTANCE = new GlobalLadder();

  @Override
  public int playersPerTeam() {
    return 0;
  }

  @Override
  public int baseValue() {
    return 0;
  }

  @Override
  public int teamsPerMatch() {
    return 0;
  }

  @Override
  public @NonNull Queue createQueue(@NonNull GuildData data) {
    throw new UnsupportedOperationException("Global ladder cannot create queues");
  }

  @Override
  public @NonNull String getName() {
    return "global";
  }

  @Override
  public @NonNull ValuesMap getOptions() {
    throw new UnsupportedOperationException("Global ladder does not have options");
  }
}
