package me.googas.api.matches.ladder;

import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.matches.queue.Queue;

/** A ladder is a ranked system that users may use to climb */
public interface Ladder {

  /**
   * Get the number of players per team
   *
   * @return the number of players per team
   */
  int playersPerTeam();

  /**
   * Get the base value which all the players start with
   *
   * @return the base value
   */
  int baseValue();

  /**
   * Get the number of teams per match
   *
   * @return the number of teams per match
   */
  int teamsPerMatch();

  /**
   * Create the queue for this ladder
   *
   * @param guildId the id of the guild to create the queue
   * @return the created queue
   */
  @NonNull
  Queue createQueue(long guildId);

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @NonNull
  String getName();

  /**
   * Get the options that the ladder uses
   *
   * @return the options in a map
   */
  @NonNull
  ValuesMap getOptions();
}
