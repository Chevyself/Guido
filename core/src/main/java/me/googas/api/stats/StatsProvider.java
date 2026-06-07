package me.googas.api.stats;

import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.utility.ImmutableCollection;

public interface StatsProvider {

  default double getWinningElo(
      @NonNull MinecraftMatchTeam winners, @NonNull Ladder ladder, @NonNull StatsProvider stats) {
    ImmutableCollection<? extends MinecraftMatchTeamMember> members = winners.getMembers();
    double sum = 0;
    if (members.isEmpty()) return sum;
    for (MinecraftMatchTeamMember member : members) {
      sum += stats.getFor(member).getElo(ladder);
    }
    return sum / members.size();
  }

  @NonNull
  Stats getFor(@NonNull MinecraftMatchTeamMember member);

  default double getLosingElo(
      @NonNull MinecraftMatchTeam winners,
      @NonNull MinecraftMatch match,
      @NonNull Ladder ladder,
      @NonNull StatsProvider stats) {
    double sum = 0;
    int size = 0;
    for (MinecraftMatchTeam team : match.getTeams()) {
      if (team == winners) continue;
      for (MinecraftMatchTeamMember member : team.getMembers()) {
        sum += stats.getFor(member).getElo(ladder);
        size++;
      }
    }
    if (size == 0) return sum;
    return sum / size;
  }
}
