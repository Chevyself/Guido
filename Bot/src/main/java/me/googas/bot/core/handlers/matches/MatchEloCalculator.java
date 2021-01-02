package me.googas.bot.core.handlers.matches;

import lombok.NonNull;
import me.googas.api.Stateable;
import me.googas.api.links.Linkable;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.bot.api.events.data.links.LinkableEloUpdatedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
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
    Match match = event.getMatch();
    if (event.getStatus() == MatchStatus.FINISHED) {
      this.setElo(match, true);
    }
  }

  /**
   * Sets the elo for the match participants
   *
   * @param match the match for the participants to set the elo
   * @param ladder the ladder which was played in the match
   * @param winnersDifference the amount of elo that winners got
   * @param losersDifference the amount of elo that the other teams lost
   * @param event whether to call the elo updated event
   */
  public void setElo(
      @NonNull Match match,
      @NonNull Ladder ladder,
      float winnersDifference,
      int losersDifference,
      boolean event) {
    MatchTeam winners = match.getWinners();
    for (MatchTeam matchTeam : match.getTeams()) {
      for (TeamMember member : matchTeam.getMembers()) {
        Linkable data = member.getLinkInfo().getLink();
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
    float previous = stateable.getElo(ladder);
    if (winner) {
      stateable.increaseElo(ladder, winnersDifference);
      stateable.increaseStat(ladderName + "-wins", 1);
    } else {
      stateable.decreaseElo(ladder, losersDifference);
      stateable.increaseStat(ladderName + "-loses", 1);
    }
    stateable.increaseStat(ladderName + "-played", 1);
    float elo = stateable.getElo(ladder);
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
    float oldElo = stateable.getElo(ladder);
    double expected = this.calculateExpected(oldElo, oldElo, ladder.baseValue());
    int winnersDifference =
        (int)
            (this.newElo(
                    oldElo,
                    expected,
                    ladder.getOptions().getOr("win-multiplier", Double.class, 1.0))
                - oldElo);
    int losersDifference =
        (int)
            (oldElo
                - this.newElo(
                    oldElo,
                    expected,
                    ladder.getOptions().getOr("lose-multiplier", Double.class, 0.0)));
    this.setElo(stateable, winner, ladder, winnersDifference, losersDifference, event);
  }

  /**
   * This method voids the match, meaning that removes the win of winners and removes the lose from
   * losers.
   *
   * @param match the match to void
   */
  public void voidMatch(@NonNull Match match, boolean setVoided) {
    MatchTeam winners = match.getWinners();
    Ladder ladder = match.getLadder();
    if (setVoided) match.setStatus(MatchStatus.VOIDED);
    if (winners == null || ladder == null) return;
    for (MatchTeam matchTeam : match.getTeams()) {
      if (matchTeam.equals(winners)) {
        for (TeamMember member : matchTeam.getMembers()) {
          member
              .getLinkInfo()
              .decreaseElo(
                  ladder,
                  match.getDetails().getOr("winners-difference", Number.class, 16).floatValue());
        }
      } else {
        for (TeamMember member : matchTeam.getMembers()) {
          member
              .getLinkInfo()
              .increaseElo(
                  ladder,
                  match.getDetails().getOr("losers-difference", Number.class, 16).floatValue());
        }
      }
    }
    match.getDetails().put("winners-difference", 0);
    match.getDetails().put("losers-difference", 0);
  }

  /**
   * Voids a match and recounts the elo of it
   *
   * @param match the match to recount
   * @param callEvents whether to all the events related to elo updates
   */
  public void recount(@NonNull Match match, boolean callEvents) {
    this.voidMatch(match, false);
    this.setElo(match, callEvents);
  }

  /**
   * Calculate the expected chances of winning for a team
   *
   * @param elo the elo of the entity which is being calculated the chances of winning
   * @param thatElo the other entity elo
   * @param ladderBase how much does a player start with in this ladder
   * @return the expected chances of winning
   */
  public double calculateExpected(float elo, float thatElo, @NonNull Number ladderBase) {
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
  public float newElo(float oldElo, double expected, double multiplier) {
    return (float) (oldElo + 32 * (multiplier - expected));
  }

  /**
   * Calculate the elo for losers
   *
   * @param match the match that finished
   * @param ladder the ladder which was played in the match
   * @return the elo of the losers
   */
  public float getLosersElo(Match match, Ladder ladder) {
    float losersElo = 0;
    int total = 0;
    MatchTeam winners = match.getWinners();
    for (MatchTeam matchTeam : match.getTeams()) {
      if (matchTeam != winners) {
        losersElo += matchTeam.getElo(ladder);
        total++;
      }
    }
    return losersElo / total;
  }

  /**
   * Sets the elo for a match
   *
   * @param match the match to set the elo
   * @param event whether to call the event of elo updated
   */
  public void setElo(@NonNull Match match, boolean event) {
    MatchTeam winners = match.getWinners();
    Ladder ladder = match.getLadder();
    if (ladder != null && winners != null) {
      float winnersElo = winners.getElo(ladder);
      float losersElo = this.getLosersElo(match, ladder);
      float newWinners =
          this.newElo(
              winnersElo,
              this.calculateExpected(winnersElo, losersElo, ladder.baseValue()),
              ladder.getOptions().getOr("win-multiplier", Double.class, 1.0));
      float newLosers =
          this.newElo(
              losersElo,
              this.calculateExpected(losersElo, winnersElo, ladder.baseValue()),
              ladder.getOptions().getOr("lose-multiplier", Double.class, 0.0));
      int winnersDifference = (int) (newWinners - winnersElo);
      int losersDifference = (int) (losersElo - newLosers);
      match.getDetails().put("winners-difference", winnersDifference);
      match.getDetails().put("losers-difference", losersDifference);
      this.setElo(match, ladder, winnersDifference, losersDifference, event);
    }
  }

  @Override
  public void close() {}
}
