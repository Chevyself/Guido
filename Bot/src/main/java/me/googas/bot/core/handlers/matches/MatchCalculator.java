package me.googas.bot.core.handlers.matches;

import me.googas.api.links.LinkedData;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.bot.core.util.console.Console;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * This handler listens to the end of a match and gives the winners the respective elo also gives
 * the ranks that were set in the server
 */
public class MatchCalculator implements GuidoEventHandler {

  /**
   * Listen to when a match ends
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.MEDIUM)
  public void onMatchStatusUpdatedEvent(@NotNull MatchStatusUpdatedEvent event) {
    Match match = event.getMatch();
    Team winners = match.getWinners();
    String ladderName = match.getDetails().getValue("ladder", String.class);
    long guildId = match.getGuildId();
    if (ladderName != null && guildId != -1) {
      Console.debug("Saving the elo for " + match);
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        if (winners != null) {
          Console.debug("There's a winner so there will be set");
          float winnersElo = winners.getElo(ladder);
          float losersElo = this.getLosersElo(match, ladder);
          double newWinners =
              this.newElo(
                  winnersElo,
                  this.calculateExpected(winnersElo, losersElo),
                  ladder.getOptions().getValueOr("win-multiplier", Integer.class, 1));
          double newLosers =
              this.newElo(
                  losersElo,
                  this.calculateExpected(losersElo, winnersElo),
                  ladder.getOptions().getValueOr("lose-multiplier", Integer.class, 0));
          double winnersDifference = newWinners - winnersElo;
          double losersDifference = losersElo - newLosers;
          match.getDetails().addValue("winners-difference", winnersDifference);
          match.getDetails().addValue("losers-difference", losersDifference);
          this.setElo(match, ladder, (float) winnersDifference, (float) losersDifference);
        }
      }
    }
  }

  /**
   * Sets the elo for the match participants
   *
   * @param match the match for the participants to set the elo
   * @param ladder the ladder which was played in the match
   * @param winnersDifference the amount of elo that winners got
   * @param losersDifference the amount of elo that the other teams lost
   */
  public void setElo(
      @NotNull Match match, Ladder ladder, float winnersDifference, float losersDifference) {
    Team winners = match.getWinners();
    String ladderName = ladder.getName();
    for (Team team : match.getTeams()) {
      for (TeamMember member : team.getMembers()) {
        LinkedData data = member.getLinkInfo().getLink();
        if (data != null) {
          data.refresh();
          Console.debug("Setting the stat and elo to " + data);
          if (team == winners) {
            data.increaseElo(ladder, winnersDifference);
            data.increaseStat(ladderName + "-wins", 1);
          } else {
            data.decreaseElo(ladder, losersDifference);
            data.increaseStat(ladderName + "-loses", 1);
          }
        }
      }
    }
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
  public double newElo(float oldElo, double expected, int multiplier) {
    return oldElo + 32 * (multiplier - expected);
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
    Team winners = match.getWinners();
    for (Team team : match.getTeams()) {
      if (team != winners) {
        losersElo += team.getElo(ladder);
        total++;
      }
    }
    return losersElo / total;
  }

  /**
   * Calculate the expected chances of winning for a team
   *
   * @param elo the elo of the entity which is being calculated the chances of winning
   * @param thatElo the other entity elo
   * @return the expected chances of winning
   */
  public double calculateExpected(float elo, float thatElo) {
    return 1 / (1 + Math.pow(10, (thatElo - elo) / 400));
  }

  @Override
  public void close() {}
}
