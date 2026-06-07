package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.time.Duration;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.stats.LeaderboardEntry;
import me.googas.api.stats.Stats;
import me.googas.api.utility.Maps;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.server.loader.GuidoLoader;

/** Commands for leaderboard */
public class LeaderboardCommands {

  private static final int LIMIT_PER_PAGE = 10;
  private static final int LIMIT_PER_PAGE_RANKING = 20;

  @Command(
      aliases = {"leaderboard", "lb"},
      description = "lb.desc")
  public Result leaderboard(
      LocaleFile locale,
      GuidoBotRuntime runtime,
      @Required(name = "lb.ladder", description = "lb.ladder.desc") Ladder ladder,
      @Free(name = "lb.page", description = "lb.page.desc", suggestions = "0") int page) {
    GuidoLoader loader = runtime.getLoader();
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("lb.not-global"));
    long max =
        loader
            .getStats()
            .maxPageLeaderboard(Stats.EMPTY_CONTEXT, ladder, LeaderboardCommands.LIMIT_PER_PAGE);
    if (page < 0) {
      page = 0;
    }
    if (page > max) {
      page = (int) max;
    }
    StringBuilder builder = new StringBuilder();
    builder.append(
        locale.get(
            "lb.title",
            Maps.builder("page", String.valueOf(page))
                .put("max", String.valueOf(max))
                .put("ladder", ladder.getName())));
    Map<Integer, LeaderboardEntry> leaderboard =
        loader.getStats().getLeaderboard(Stats.EMPTY_CONTEXT, ladder, page, 10);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "lb.entry",
                    Maps.builder("display", data.getDisplay())
                        .put("index", String.valueOf(index))
                        .put("elo", String.valueOf((int) data.getElo(ladder)))
                        .put("wins", String.valueOf((int) data.getWins(ladder)))
                        .put("loses", String.valueOf((int) data.getLoses(ladder))))));
    return Result.of(builder.toString(), Duration.ofSeconds(30));
  }

  @Command(
      aliases = {"ranking", "table"},
      description = "table.desc")
  public Result ranking(
      LocaleFile locale,
      GuidoBotRuntime runtime,
      @Required(name = "table.stat", description = "table.stat.desc") String stat,
      @Free(name = "table.page", description = "table.page.desc", suggestions = "0") int page) {
    GuidoLoader loader = runtime.getLoader();
    long max = loader.getStats().maxPageLeaderboard(Stats.EMPTY_CONTEXT, stat, 20);
    if (page < 0) {
      page = 0;
    }
    if (page > max) {
      page = (int) max;
    }
    StringBuilder builder = new StringBuilder();
    builder.append(
        locale.get(
            "table.title",
            Maps.builder("page", String.valueOf(page))
                .put("max", String.valueOf(max))
                .put("stat", stat)));
    Map<Integer, LeaderboardEntry> leaderboard =
        loader
            .getStats()
            .getLeaderboard(
                Stats.EMPTY_CONTEXT, stat, page, LeaderboardCommands.LIMIT_PER_PAGE_RANKING);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "table.entry",
                    Maps.builder("display", data.getDisplay())
                        .put("index", String.valueOf(index))
                        .put("stat", String.valueOf((int) data.getStat(stat))))));
    return Result.of(builder.toString());
  }
}
