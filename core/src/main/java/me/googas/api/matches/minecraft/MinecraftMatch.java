package me.googas.api.matches.minecraft;

import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.matches.MatchStatus;

public interface MinecraftMatch {
  @NonNull
  UUID getId();

  @NonNull
  Set<MinecraftMatchTeam> getTeams();

  @NonNull
  MatchStatus getStatus();

  /**
   * Get the index of the winning team
   *
   * @return the index or -1 if none
   */
  int getTeamWinner();

  @NonNull
  String getLadderName();

  /**
   * Get all the team members
   *
   * @return a set containing all the members of all {@link #getTeams()}
   */
  @NonNull
  Set<MinecraftMatchTeamMember> getParticipants();
}
