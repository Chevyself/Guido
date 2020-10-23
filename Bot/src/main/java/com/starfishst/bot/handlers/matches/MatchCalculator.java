package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.data.BotTeam;
import com.starfishst.bot.api.events.data.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.matches.Ladder;
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
    BotMatch match = event.getMatch();
    BotTeam winners = match.getWinners();
    Long guildId = match.getDetails().getValue("guild", Long.class);
    String ladderName = match.getDetails().getValue("ladder", String.class);
    if (guildId != null && ladderName != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildData(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        if (winners != null) {
          float winnersElo = winners.getElo(ladder);
          float losersElo = 0;
          for (BotTeam team : match.getTeams()) {
            if (team != winners) {
              losersElo += team.getElo(ladder);
            }
          }
          double winnersExpected = 1 / (1 + Math.pow(10, (losersElo - winnersElo) / 400));
          double losersExpected = 1 / (1 + Math.pow(10, (winnersElo - losersElo) / 400));
          double newWinners =
              winnersElo
                  + 32
                      * (ladder.getOptions().getValueOr("win-multiplier", Double.class, 1D)
                          - winnersExpected);
          double newLosers =
              losersElo
                  + 32
                      * (ladder.getOptions().getValueOr("lose-multiplier", Double.class, 0D)
                          - losersExpected);
          double winnersDifference = newWinners - winnersElo;
          double losersDifference = losersElo - newLosers;
          match.getDetails().addValue("winners-difference", winnersDifference);
          match.getDetails().addValue("losers-difference", losersDifference);

          // Set the stats
          for (BotTeam team : event.getMatch().getTeams()) {
            for (BotLinkedInfo member : team.getMembers().keySet()) {
              BotLinkedData data = member.getData();
              if (data != null) {
                if (team == winners) {
                  data.increaseElo(ladder, winnersDifference);
                } else {
                  data.decreaseElo(ladder, losersDifference);
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void close() {}
}
