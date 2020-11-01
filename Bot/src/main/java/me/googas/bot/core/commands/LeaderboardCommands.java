package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.List;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkedData;
import me.googas.api.matches.GlobalLadder;
import me.googas.api.matches.Ladder;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.core.Guido;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;

/** Commands for leaderboard */
public class LeaderboardCommands {

  /**
   * Get the leaderboard
   *
   * @param locale the locale of the sender
   * @param ladder the ladder to see the leaderboard
   * @param page the page to see
   * @return the leaderboard
   */
  @Command(
      aliases = {"leaderboard", "lb"},
      description = "lb.desc",
      time = "30s")
  public Result leaderboard(
      LocaleFile locale,
      @Required(name = "lb.ladder", description = "lb.ladder.desc") Ladder ladder,
      @Optional(name = "lb.page", description = "lb.page.desc", suggestions = "0") int page) {
    BotDataLoader loader = Guido.getDataLoader();
    if (!(ladder instanceof GlobalLadder)) {
      long max = loader.maxPageLeaderboard(ladder, 10);
      if (page < 0) {
        page = 0;
      }
      if (page > max) {
        page = (int) max;
      }
      StringBuilder builder = Strings.getBuilder();
      builder.append(
          locale.get(
              "lb.title",
              Maps.builder("page", String.valueOf(page))
                  .append("max", String.valueOf(max))
                  .append("ladder", ladder.getName())));
      List<LinkedData> leaderboard = loader.getLeaderboard(ladder, page, 10);
      for (LinkedData data : leaderboard) {
        builder.append(
            locale.get(
                "lb.entry",
                Maps.builder("single", data.getSingle())
                    .append("elo", String.valueOf((int) data.getElo(ladder)))
                    .append("wins", String.valueOf((int) data.getWins(ladder)))
                    .append("loses", String.valueOf((int) data.getLoses(ladder)))));
      }
      return new Result(builder.toString());
    } else {
      return new Result(ResultType.USAGE, locale.get("lb.not-global"));
    }
  }
}
