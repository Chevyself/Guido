package me.googas.bot.core.handlers.matches;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.Stateable;
import me.googas.api.events.links.LinkableEloUpdatedEvent;
import me.googas.api.events.match.MatchStatusUpdatedEvent;
import me.googas.api.links.Linkable;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;

/**
 * This handler listens to the end of a match and gives the winners the respective elo also gives
 * the ranks that were set in the server
 */
public class MatchEloCalculator implements GuidoHandler {

  /**
   * Listen to when a match ends
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.MEDIUM)
  public void onMatchStatusUpdatedEvent(@NonNull MatchStatusUpdatedEvent event) {
    AbstractMatch abstractMatch = event.getAbstractMatch();
    if (event.getStatus() == MatchStatus.FINISHED) {
      this.setElo(abstractMatch, true);
    }
  }

  /**
   * Sets the elo for the abstractMatch participants
   *
   * @param abstractMatch the abstractMatch for the participants to set the elo
   * @param ladder the ladder which was played in the abstractMatch
   * @param winnersDifference the amount of elo that winners got
   * @param losersDifference the amount of elo that the other teams lost
   * @param event whether to call the elo updated event
   */
  public void setElo(
      @NonNull AbstractMatch abstractMatch,
      @NonNull Ladder ladder,
      float winnersDifference,
      int losersDifference,
      boolean event) {
    MatchTeam winners = abstractMatch.getWinners();
    for (MatchTeam matchTeam : abstractMatch.getTeams()) {
      for (TeamMember member : matchTeam.getMembers()) {
        Linkable data = member.getLink().getLink();
        if (data != null) {
          this.setElo(
              data, matchTeam.equals(winners), ladder, winnersDifference, losersDifference, event);
        }
      }
    }
  }

  public void setElo(
      @NonNull Stateable stateable,
      boolean winner,
      @NonNull Ladder ladder,
      float winnersDifference,
      int losersDifference,
      boolean event) {
    String ladderName = ladder.getName();
    double previous = stateable.getElo("none", ladder);
    if (winner) {
      stateable.increaseElo("none", ladder, winnersDifference);
      stateable.increaseStat("none", ladderName + "-wins", 1);
    } else {
      stateable.decreaseElo("none", ladder, losersDifference);
      stateable.increaseStat("none", ladderName + "-loses", 1);
    }
    stateable.increaseStat("none", ladderName + "-played", 1);
    double elo = stateable.getElo("none", ladder);
    if (stateable instanceof Linkable && event)
      new LinkableEloUpdatedEvent((Linkable) stateable, ladder, previous, elo, winner).call();
  }

  /**
   * Sets the elo of an stateable but calculated based in its own elo for cases such as a double
   * loss
   *
   * @param stateable the stateable to set the elo
   * @param winner whether to give it a win or a lose
   * @param ladder the ladder in which to set the elo
   * @param event whether to call the event of elo updated
   */
  public void setElo(
      @NonNull Stateable stateable, boolean winner, @NonNull Ladder ladder, boolean event) {
    double oldElo = stateable.getElo("none", ladder);
    double expected = this.calculateExpected(oldElo, oldElo, ladder.baseValue());
    int winnersDifference =
        (int)
            ((this.newElo(oldElo, expected, 1) - oldElo)
                * ladder.getDouble("global", "win-multiplier", 1));
    int losersDifference =
        (int)
            ((oldElo - this.newElo(oldElo, expected, 0))
                * ladder.getDouble("global", "lose-multiplier", 1));
    this.setElo(stateable, winner, ladder, winnersDifference, losersDifference, event);
  }

  /**
   * This method voids the abstractMatch, meaning that removes the win of winners and removes the
   * lose from losers.
   *
   * @param abstractMatch the abstractMatch to void
   */
  public void voidMatch(@NonNull AbstractMatch abstractMatch, boolean setVoided) {
    MatchTeam winners = abstractMatch.getWinners();
    Ladder ladder = abstractMatch.getLadder();
    if (setVoided) abstractMatch.setStatus(MatchStatus.VOIDED);
    if (winners == null || ladder == null) return;
    for (MatchTeam matchTeam : abstractMatch.getTeams()) {
      if (matchTeam.equals(winners)) {
        for (TeamMember member : matchTeam.getMembers()) {
          member
              .getLink()
              .decreaseElo("none", ladder, abstractMatch.getFloat(null, "winners-difference", 16));
        }
      } else {
        for (TeamMember member : matchTeam.getMembers()) {
          member
              .getLink()
              .increaseElo("none", ladder, abstractMatch.getFloat(null, "losers-difference", 16));
        }
      }
    }
    abstractMatch.set(null, "winners-difference", 0);
    abstractMatch.set(null, "losers-difference", 0);
  }

  /**
   * Voids a abstractMatch and recounts the elo of it
   *
   * @param abstractMatch the abstractMatch to recount
   * @param callEvents whether to all the events related to elo updates
   */
  public void recount(@NonNull AbstractMatch abstractMatch, boolean callEvents) {
    this.voidMatch(abstractMatch, false);
    this.setElo(abstractMatch, callEvents);
  }

  public void setNewSeasonElo(@NonNull Stateable stateable, @NonNull Collection<Ladder> ladders) {
    for (Ladder ladder : ladders) {
      this.setNewSeasonElo(stateable, ladder);
    }
  }

  public void setNewSeasonElo(@NonNull Stateable stateable, @NonNull Ladder ladder) {
    double wins = stateable.getWins("none", ladder);
    double loses = stateable.getLoses("none", ladder);
    float elo = ladder.baseValue();
    for (int i = 0; i < wins; i++) {
      double expected = this.calculateExpected(elo, elo, ladder.baseValue());
      int difference =
          (int)
              ((this.newElo(elo, expected, 1) - elo)
                  * ladder.getDouble("global", "lose-multiplier", 1));
      elo += difference;
    }
    for (int i = 0; i < loses; i++) {
      double expected = this.calculateExpected(elo, elo, ladder.baseValue());
      int difference =
          (int)
              ((elo - this.newElo(elo, expected, 1))
                  * ladder.getDouble("global", "lose-multiplier", 1));
      elo -= -difference;
    }
    // TODO set elo using a real context such as a season
    stateable.setElo("global", ladder, elo);
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
   * Calculate the elo for losers
   *
   * @param abstractMatch the abstractMatch that finished
   * @param ladder the ladder which was played in the abstractMatch
   * @return the elo of the losers
   */
  public float getLosersElo(AbstractMatch abstractMatch, Ladder ladder) {
    float losersElo = 0;
    int total = 0;
    MatchTeam winners = abstractMatch.getWinners();
    for (MatchTeam matchTeam : abstractMatch.getTeams()) {
      if (matchTeam != winners) {
        losersElo += matchTeam.getElo(ladder);
        total++;
      }
    }
    return losersElo / total;
  }

  /**
   * Sets the elo for a abstractMatch
   *
   * @param abstractMatch the abstractMatch to set the elo
   * @param event whether to call the event of elo updated
   */
  public void setElo(@NonNull AbstractMatch abstractMatch, boolean event) {
    MatchTeam winners = abstractMatch.getWinners();
    Ladder ladder = abstractMatch.getLadder();
    if (ladder != null && winners != null) {
      float winnersElo = winners.getElo(ladder);
      float losersElo = this.getLosersElo(abstractMatch, ladder);
      float newWinners =
          this.newElo(
              winnersElo, this.calculateExpected(winnersElo, losersElo, ladder.baseValue()), 1);
      float newLosers =
          this.newElo(
              losersElo, this.calculateExpected(losersElo, winnersElo, ladder.baseValue()), 0);
      int winnersDifference =
          (int) ((newWinners - winnersElo) * ladder.getDouble("global", "win-multiplier", 1));
      int losersDifference =
          (int) ((losersElo - newLosers) * ladder.getDouble("global", "lose-multiplier", 1));
      abstractMatch.set(null, "winners-difference", winnersDifference);
      abstractMatch.set(null, "losers-difference", losersDifference);
      this.setElo(abstractMatch, ladder, winnersDifference, losersDifference, event);
    }
  }

  @Override
  public void onDisable() {}
}
