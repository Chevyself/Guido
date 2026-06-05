package me.googas.bot.core.matches.ladder;

import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.matches.queue.GuidoPGMQueue;

/** An implementation for ladder */
// TODO move to generics
public class GuidoLadder implements PlayableLadder {

  @NonNull @Getter private final String name;
  private final int playersPerTeam;
  private final int baseValue;
  private final int teamsPerMatch;
  @Getter private final MinecraftTeamSelectionType teamSelectionType;

  public GuidoLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseValue,
      int teamsPerMatch,
      MinecraftTeamSelectionType teamSelectionType) {
    this.name = name;
    this.playersPerTeam = playersPerTeam;
    this.baseValue = baseValue;
    this.teamsPerMatch = teamsPerMatch;
    this.teamSelectionType = teamSelectionType;
  }

  @Override
  public int playersPerTeam() {
    return this.playersPerTeam;
  }

  @Override
  public int baseValue() {
    return this.baseValue;
  }

  @Override
  public int teamsPerMatch() {
    return this.teamsPerMatch;
  }

  @Override
  public @NonNull MinecraftQueue createQueue(@NonNull GuidoBotRuntime runtime) {
    return new GuidoPGMQueue(this.getName(), runtime);
  }
}
