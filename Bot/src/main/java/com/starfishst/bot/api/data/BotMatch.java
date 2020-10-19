package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.matches.Match;
import java.util.Collection;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An extension of match */
public interface BotMatch extends Match, ICatchable {

  /**
   * Get the teams that are participating in the match
   *
   * @return collection of teams
   */
  @Override
  @NotNull
  Collection<? extends BotTeam> getTeams();

  /**
   * Get the team that won the match
   *
   * @return the team that won the match. This can return null in case that the match has not
   *     finished yet
   */
  @Override
  @Nullable
  BotTeam getWinners();
}
