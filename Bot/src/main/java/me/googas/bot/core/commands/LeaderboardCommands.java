package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.bot.Guido;
import me.googas.bot.api.types.loader.BotDataLoader;
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
    if (ladder instanceof GlobalLadder)
      return new Result(ResultType.USAGE, locale.get("lb.not-global"));
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
    Map<Integer, LinkableInfo> leaderboard = loader.getLeaderboard(ladder, page, 10);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "lb.entry",
                    Maps.builder("single", data.getSingle())
                        .append("index", String.valueOf(index))
                        .append("elo", String.valueOf((int) data.getElo(ladder)))
                        .append("wins", String.valueOf((int) data.getWins(ladder)))
                        .append("loses", String.valueOf((int) data.getLoses(ladder))))));
    return new Result(builder.toString());
  }

  @Command(
      aliases = {"ranking", "table"},
      description = "table.desc",
      time = "30s")
  public Result ranking(
      LocaleFile locale,
      @Required(name = "table.stat", description = "table.stat.desc") String stat,
      @Optional(name = "table.page", description = "table.page.desc", suggestions = "0") int page) {
    BotDataLoader loader = Guido.getDataLoader();
    long max = loader.maxPageLeaderboard(stat, 20);
    if (page < 0) {
      page = 0;
    }
    if (page > max) {
      page = (int) max;
    }
    StringBuilder builder = Strings.getBuilder();
    builder.append(
        locale.get(
            "table.title",
            Maps.builder("page", String.valueOf(page))
                .append("max", String.valueOf(max))
                .append("stat", stat)));
    Map<Integer, LinkableInfo> leaderboard = loader.getLeaderboard(stat, page, 20, false);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "table.entry",
                    Maps.builder("single", data.getSingle())
                        .append("index", String.valueOf(index))
                        .append("stat", String.valueOf((int) data.getStat(stat))))));
    return new Result(builder.toString());
  }
}
