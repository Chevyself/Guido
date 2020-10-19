package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An extension of team */
public interface BotTeam extends Team {

  /**
   * Get the members of the team
   *
   * @return the members of the team
   */
  @Override
  @NotNull
  Map<? extends BotLinkedInfo, TeamRole> getMembers();
}
