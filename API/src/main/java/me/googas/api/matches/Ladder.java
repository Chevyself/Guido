package me.googas.api.matches;

import me.googas.api.discord.GuildData;
import me.googas.api.utility.ValuesMap;
import org.jetbrains.annotations.NotNull;

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
   * Create the queue for this ladder
   *
   * @param guild the guild that the queue is being created to
   * @return the created queue
   */
  @NotNull
  Queue createQueue(@NotNull GuildData guild);

  /**
   * Get the name of the ladder
   *
   * @return the name of the ladder
   */
  @NotNull
  String getName();

  /**
   * Get the options that the ladder uses
   *
   * @return the options in a map
   */
  @NotNull
  ValuesMap getOptions();
}
