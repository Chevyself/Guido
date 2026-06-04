package me.googas.api.matches.ladder;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.matches.queue.Queue;

/** The global ladder cannot be edited, this ladder calculates the global elo of the linked data */
public class GlobalLadder implements Ladder {

  /** A public static instance for global ladders */
  public static final Ladder INSTANCE = new GlobalLadder();

  @Override
  public @NonNull Map<String, Map<String, Object>> getInformation() {
    return new HashMap<>();
  }

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
  public @NonNull Queue createQueue(long guildId) {
    throw new UnsupportedOperationException("Global ladder cannot create queues");
  }

  @Override
  public @NonNull String getName() {
    return "global";
  }
}
