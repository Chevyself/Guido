package me.googas.api.matches;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.utility.ImmutableCollection;
import net.dv8tion.jda.api.EmbedBuilder;

public interface Match {

  @NonNull
  UUID getId();

  @NonNull
  ImmutableCollection<? extends MatchTeam> getTeams();

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
  ImmutableCollection<? extends MatchTeamMember> getParticipants();

  int getWinnersDifference();

  int getLosersDifference();

  @NonNull
  default Optional<MatchTeam> getWinners() {
    int teamWinner = this.getTeamWinner();
    MatchTeam winner = null;
    if (teamWinner != -1) {
      for (MatchTeam team : this.getTeams()) {
        if (team.getId() == teamWinner) {
          winner = team;
          break;
        }
      }
    }
    return Optional.ofNullable(winner);
  }

  void appendDetails(@NonNull EmbedBuilder builder);

  void finish(int winningTeam);

  default void finish() {
    this.finish(MatchTeam.NO_TEAM);
  }

  @NonNull
  default Optional<? extends MatchTeam> getTeamByName(String name) {
    MatchTeam team = null;
    for (MatchTeam thisTeam : this.getTeams()) {
      if (thisTeam.getName().equalsIgnoreCase(name)) {
        team = thisTeam;
        break;
      }
    }
    return Optional.ofNullable(team);
  }

  int indexOf(@NonNull MatchTeam matchTeam);

  default void finish(@NonNull MatchTeam matchTeam) {
    this.finish(this.indexOf(matchTeam));
  }

  void setWinnersDifference(int winnersDifference);

  void setLosersDifference(int losersDifference);

  void setStatus(@NonNull MatchStatus matchStatus);
}
