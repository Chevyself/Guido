package me.googas.api.matches.minecraft;

import java.util.List;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.utility.ImmutableCollection;

public interface MinecraftMatch extends Match {

  @NonNull
  ImmutableCollection<? extends MinecraftMatchTeam> getTeams();

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
  ImmutableCollection<? extends MinecraftMatchTeamMember> getParticipants();

  void setServer(@NonNull String name);

  @NonNull
  String getServer();

  @NonNull
  String getMap();

  void setMap(@NonNull String mapName);

  int addTeam(@NonNull MinecraftMatchTeam team);

  List<? extends MinecraftMatchTeam> setTeams(@NonNull List<? extends MinecraftMatchTeam> teams);
}
