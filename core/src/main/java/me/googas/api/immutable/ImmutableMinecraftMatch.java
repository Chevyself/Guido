package me.googas.api.immutable;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.utility.ImmutableCollection;
import net.dv8tion.jda.api.EmbedBuilder;

public class ImmutableMinecraftMatch implements MinecraftMatch {

  @NonNull
  @Getter
  @SerializedName("_id")
  private final UUID id;

  @NonNull private final List<ImmutableMinecraftMatchTeam> teams;
  @NonNull @Getter private final MatchStatus status;
  @Getter private final int teamWinner;
  @NonNull @Getter private final String ladderName;
  @Getter private final int winnersDifference;
  @Getter private final int losersDifference;

  public ImmutableMinecraftMatch(
      @NonNull UUID id,
      @NonNull List<ImmutableMinecraftMatchTeam> teams,
      @NonNull MatchStatus status,
      int teamWinner,
      @NonNull String ladderName,
      int winnersDifference,
      int losersDifference) {
    this.id = id;
    this.teams = teams;
    this.status = status;
    this.teamWinner = teamWinner;
    this.ladderName = ladderName;
    this.winnersDifference = winnersDifference;
    this.losersDifference = losersDifference;
  }

  public ImmutableMinecraftMatch(@NonNull MinecraftMatch other) {
    this(
        other.getId(),
        ImmutableMinecraftMatchTeam.from(other.getTeams()),
        other.getStatus(),
        other.getTeamWinner(),
        other.getLadderName(),
        other.getWinnersDifference(),
        other.getLosersDifference());
  }

  @Override
  public @NonNull ImmutableCollection<ImmutableMinecraftMatchTeam> getTeams() {
    return new ImmutableCollection<>(teams);
  }

  @Override
  public @NonNull ImmutableCollection<ImmutableMinecraftMatchTeamMember> getParticipants() {
    return ImmutableCollection.flatMap(this.teams, ImmutableMinecraftMatchTeam::getMembers);
  }

  @Override
  public void appendDetails(@NonNull EmbedBuilder builder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void finish(int winningTeam) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(@NonNull MatchTeam matchTeam) {
    for (int i = 0; i < this.teams.size(); i++) {
      MatchTeam team = this.teams.get(i);
      if (matchTeam.getId() == team.getId()) return i;
    }
    return -1;
  }

  @Override
  public void setWinnersDifference(int winnersDifference) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLosersDifference(int losersDifference) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setStatus(@NonNull MatchStatus matchStatus) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setServer(@NonNull String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return "ImmutableMinecraftMatch{"
        + "id="
        + id
        + ", teams="
        + teams
        + ", status="
        + status
        + ", teamWinner="
        + teamWinner
        + ", ladderName='"
        + ladderName
        + '\''
        + ", winnersDifference="
        + winnersDifference
        + ", losersDifference="
        + losersDifference
        + '}';
  }
}
