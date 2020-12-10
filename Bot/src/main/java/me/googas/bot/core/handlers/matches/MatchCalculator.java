package me.googas.bot.core.handlers.matches;

import java.util.logging.Level;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.bot.core.util.GuidoLogBuilder;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;

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
  public void onMatchStatusUpdatedEvent(@NonNull MatchStatusUpdatedEvent event) {
    GuidoLogBuilder builder = new GuidoLogBuilder(Level.INFO);
    Match match = event.getMatch();
    Team winners = match.getWinners();
    String ladderName = match.getDetails().get("ladder", String.class);
    long guildId = match.getGuildId();
    builder.append("Checking if event is FINISHED and match");
    if (event.getStatus() == MatchStatus.FINISHED && ladderName != null && guildId != -1) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      builder.append("\n Match is finished checking ladder, winners and guild id");
      if (ladder != null && winners != null) {
        builder.append("\n").append("There's a ladder and winners");
        float winnersElo = winners.getElo(ladder);
        float losersElo = this.getLosersElo(match, ladder);
        float newWinners =
            this.newElo(
                winnersElo,
                this.calculateExpected(winnersElo, losersElo),
                ladder.getOptions().getOr("win-multiplier", Double.class, 1.0));
        float newLosers =
            this.newElo(
                losersElo,
                this.calculateExpected(losersElo, winnersElo),
                ladder.getOptions().getOr("lose-multiplier", Double.class, 0.0));
        int winnersDifference = Math.round(newWinners - winnersElo);
        int losersDifference = Math.round(losersElo - newLosers);
        match.getDetails().put("winners-difference", winnersDifference);
        match.getDetails().put("losers-difference", losersDifference);
        this.setElo(match, ladder, winnersDifference, losersDifference);
        builder.append("\n Elo has been set");
      }
    }
    builder.send();
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
      @NonNull Match match, Ladder ladder, float winnersDifference, int losersDifference) {
    Team winners = match.getWinners();
    String ladderName = ladder.getName();
    for (Team team : match.getTeams()) {
      for (TeamMember member : team.getMembers()) {
        Linkable data = member.getLinkInfo().getLink();
        if (data != null) {
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
   * This method voids the match, meaning that removes the win of winners and removes the lose from
   * losers.
   *
   * @param match the match to void
   */
  public void voidMatch(@NonNull Match match) {
    Team winners = match.getWinners();
    Ladder ladder = match.getLadder();
    if (winners == null || ladder == null) return;
    for (Team team : match.getTeams()) {
      if (team.equals(winners)) {
        for (TeamMember member : team.getMembers()) {
          member
              .getLinkInfo()
              .decreaseElo(ladder, match.getDetails().getOr("", Number.class, 16).floatValue());
        }
      } else {
        for (TeamMember member : team.getMembers()) {
          member
              .getLinkInfo()
              .increaseElo(ladder, match.getDetails().getOr("", Number.class, 16).floatValue());
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
  public float newElo(float oldElo, double expected, double multiplier) {
    return (float) Math.floor(oldElo + 32 * (multiplier - expected));
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
