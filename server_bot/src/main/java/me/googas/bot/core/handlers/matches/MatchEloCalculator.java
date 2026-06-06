package me.googas.bot.core.handlers.matches;

import java.util.Collection;
import java.util.Optional;

import lombok.NonNull;
import me.googas.api.events.links.LinkableEloUpdatedEvent;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.stats.Stats;
import me.googas.api.stats.StatsProvider;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;

/**
 * This handler listens to the end of a match and gives the winners the respective elo also gives
 * the ranks that were set in the server
 */
public class MatchEloCalculator implements GuidoHandler {

  @NonNull
  private final GuidoBotRuntime runtime;

    public MatchEloCalculator(@NonNull GuidoBotRuntime runtime) {
        this.runtime = runtime;
    }

    /**
   * Listen to when a match ends
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.MEDIUM)
  public void onMatchStatusUpdatedEvent(@NonNull MinecraftMatchStatusUpdatedEvent event) {
    MinecraftMatch match = event.getMatch();
    if (event.getStatus() == MatchStatus.FINISHED) {
      this.setElo(match, true);
    }
  }

  /**
   * Sets the elo for the match participants
   *
   * @param match             the match for the participants to set the elo
   * @param winners           the winning team
   * @param ladder            the ladder which was played in the match
   * @param winnersDifference the amount of elo that winners got
   * @param losersDifference  the amount of elo that the other teams lost
   * @param event             whether to call the elo updated event
   */
  public void setElo(
          @NonNull MinecraftMatch match,
          @NonNull MinecraftMatchTeam winners,
          @NonNull Ladder ladder,
          float winnersDifference,
          int losersDifference,
          boolean event) {
    for (MinecraftMatchTeam matchTeam : match.getTeams()) {
      for (MinecraftMatchTeamMember member : matchTeam.getMembers()) {
        member.getLinkable(runtime.getLinkableMatcher())
                .ifPresent(data -> {
                  this.setElo(
                          data, null, matchTeam.equals(winners), ladder, winnersDifference, losersDifference, event);
                });
      }
    }
  }

  public void setElo(
      @NonNull MinecraftLinkable minecraftLink,
      Stats stats,
      boolean winner,
      @NonNull Ladder ladder,
      float winnersDifference,
      float losersDifference,
      boolean event) {
    if (stats == null) {
      stats = runtime.getLoader().getStats().getForMinecraftLink(minecraftLink, Stats.EMPTY_CONTEXT);
    }
    double previous = stats.getElo(ladder);
    if (winner) {
      stats.increaseElo(ladder, winnersDifference);
      stats.increaseWins(ladder, 1);
    } else {
      stats.decreaseElo(ladder, losersDifference);
      stats.increaseLoses(ladder, 1);
    }
    stats.increasePlayed(ladder, 1);
    double elo = stats.getElo(ladder);
    if (event) {
      LinkableEloUpdatedEvent newEvent = new LinkableEloUpdatedEvent(minecraftLink, ladder, previous, elo, winner);
      runtime.getListeners().call(newEvent);
    }
  }

  /**
   * Sets the elo of an minecraftLink but calculated based in its own elo for cases such as a double
   * loss
   *
   * @param minecraftLink the minecraftLink to set the elo
   * @param winner whether to give it a win or a lose
   * @param ladder the ladder in which to set the elo
   * @param event whether to call the event of elo updated
   */
  public void setElo(
      @NonNull MinecraftLinkable minecraftLink, boolean winner, @NonNull Ladder ladder, boolean event) {
    Stats stats = runtime.getLoader().getStats().getForMinecraftLink(minecraftLink, Stats.EMPTY_CONTEXT);
    double oldElo = stats.getElo(ladder);
    double expected = this.calculateExpected(oldElo, oldElo, ladder.baseValue());
    int winnersDifference =
        (int)
            ((this.newElo(oldElo, expected, 1) - oldElo)
                * ladder.getWinMultiplier());
    int losersDifference =
        (int)
            ((oldElo - this.newElo(oldElo, expected, 0))
                * ladder.getLoseMultiplier());
    this.setElo(minecraftLink, stats, winner, ladder, winnersDifference, losersDifference, event);
  }

  /**
   * This method voids the match, meaning that removes the win of winners and removes the
   * lose from losers.
   *
   * @param match the match to void
   */
  public void voidMatch(@NonNull MinecraftMatch match, boolean setVoided) {
    Optional<MatchTeam> optionalWinners = match.getWinners();
    Optional<Ladder> optionalLadder = runtime.getLadderProvider().getByName(match.getLadderName());
    if (setVoided) match.setStatus(MatchStatus.VOIDED);
    if (optionalWinners.isEmpty() || optionalLadder.isEmpty()) return;
    MatchTeam winners = optionalWinners.get();
    Ladder ladder = optionalLadder.get();
    for (MinecraftMatchTeam matchTeam : match.getTeams()) {
      if (matchTeam.equals(winners)) {
        for (MinecraftMatchTeamMember member : matchTeam.getMembers()) {
          this.runtime
                  .getStatsProvider()
                  .getFor(member)
                          .decreaseElo(ladder, match.getWinnersDifference());
        }
      } else {

        for (MinecraftMatchTeamMember member : matchTeam.getMembers()) {
          this.runtime
                  .getStatsProvider()
                  .getFor(member)
                  .increaseElo(ladder, match.getLosersDifference());
        }
      }
    }
  }

  /**
   * Voids a abstractMatch and recounts the elo of it
   *
   * @param abstractMatch the abstractMatch to recount
   * @param callEvents whether to all the events related to elo updates
   */
  public void recount(@NonNull MinecraftMatch abstractMatch, boolean callEvents) {
    this.voidMatch(abstractMatch, false);
    this.setElo(abstractMatch, callEvents);
  }


  /**
   * Calculate the expected chances of winning for a team
   *
   * @param elo the elo of the entity which is being calculated the chances of winning
   * @param thatElo the other entity elo
   * @param ladderBase how much does a player start with in this ladder
   * @return the expected chances of winning
   */
  public double calculateExpected(double elo, double thatElo, @NonNull Number ladderBase) {
    return 1 / (1 + Math.pow(10, (thatElo - elo) / ladderBase.intValue()));
  }

  /**
   * Calculate new elo
   *
   * @param oldElo the old elo
   * @param expected the chances to win
   * @param multiplier the multiplier it can depend on whether it is a win or lose. It is used to
   *     give a different amount of elo
   * @return the new elo
   */
  public float newElo(double oldElo, double expected, double multiplier) {
    return (float) (oldElo + 32 * (multiplier - expected));
  }

  /**
   * Sets the elo for a match
   *
   * @param match the match to set the elo
   * @param event whether to call the event of elo updated
   */
  public void setElo(@NonNull MinecraftMatch match, boolean event) {
    Optional<MatchTeam> optionalWinners = match.getWinners();
    Optional<Ladder> optionalLadder = runtime.getLadderProvider().getByName(match.getLadderName());
    if (optionalLadder.isEmpty() || optionalWinners.isEmpty()) return;
    MatchTeam matchTeam = optionalWinners.get();
    Ladder ladder = optionalLadder.get();
    if (!(matchTeam instanceof MinecraftMatchTeam winners)) return;
    StatsProvider stats = this.runtime.getStatsProvider();
    double winnersElo = stats.getWinningElo(winners, ladder, runtime.getStatsProvider());
    double losersElo = stats.getLosingElo(winners, match, ladder, runtime.getStatsProvider());
    float newWinners =
            this.newElo(
                    winnersElo, this.calculateExpected(winnersElo, losersElo, ladder.baseValue()), 1);
    float newLosers =
            this.newElo(
                    losersElo, this.calculateExpected(losersElo, winnersElo, ladder.baseValue()), 0);
    int winnersDifference =
            (int) ((newWinners - winnersElo) * ladder.getWinMultiplier());
    int losersDifference =
            (int) ((losersElo - newLosers) * ladder.getLoseMultiplier());
    match.setWinnersDifference(winnersDifference);
    match.setLosersDifference(losersDifference);
    this.setElo(match, winners, ladder, winnersDifference, losersDifference, event);
  }

  @Override
  public void onDisable() {}
}
