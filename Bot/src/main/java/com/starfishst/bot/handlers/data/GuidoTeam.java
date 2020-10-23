package com.starfishst.bot.handlers.data;

import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.api.data.BotTeam;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An implementation for team */
public class GuidoTeam implements BotTeam {

  /** The members of the team */
  @NotNull private final HashMap<BotLinkedInfo, TeamRole> members;

  /** The name of the team */
  @NotNull private final String name;

  /**
   * Create the team
   *
   * @param members the members inside the team
   * @param name the name of the team
   */
  public GuidoTeam(@NotNull HashMap<BotLinkedInfo, TeamRole> members, @NotNull String name) {
    this.members = members;
    this.name = name;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull Map<BotLinkedInfo, TeamRole> getMembers() {
    return this.members;
  }
}
