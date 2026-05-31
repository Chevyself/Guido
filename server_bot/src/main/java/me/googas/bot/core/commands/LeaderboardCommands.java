package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.time.Duration;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.loader.GuidoLoader;

/** Commands for leaderboard */
public class LeaderboardCommands {

  @Command(
      aliases = {"leaderboard", "lb"},
      description = "lb.desc")
  public Result leaderboard(
      LocaleFile locale,
      @Required(name = "lb.ladder", description = "lb.ladder.desc") Ladder ladder,
      @Free(name = "lb.page", description = "lb.page.desc", suggestions = "0") int page) {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("lb.not-global"));
    long max = loader.getLinks().maxPageLeaderboard(ladder, 10);
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
    Map<Integer, LinkableInfo> leaderboard = loader.getLinks().getLeaderboard(ladder, page, 10);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "lb.entry",
                    Maps.builder("single", data.getSingle())
                        .put("index", String.valueOf(index))
                        .put("elo", String.valueOf((int) data.getElo("none", ladder)))
                        .put("wins", String.valueOf((int) data.getWins("none", ladder)))
                        .put("loses", String.valueOf((int) data.getLoses("none", ladder))))));
    return Result.of(builder.toString(), Duration.ofSeconds(30));
  }

  @Command(
      aliases = {"ranking", "table"},
      description = "table.desc")
  public Result ranking(
      LocaleFile locale,
      @Required(name = "table.stat", description = "table.stat.desc") String stat,
      @Free(name = "table.page", description = "table.page.desc", suggestions = "0") int page) {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    long max = loader.getLinks().maxPageLeaderboard(stat, 20);
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
    Map<Integer, LinkableInfo> leaderboard =
        loader.getLinks().getLeaderboard(stat, page, 20, false);
    leaderboard.forEach(
        (index, data) ->
            builder.append(
                locale.get(
                    "table.entry",
                    Maps.builder("single", data.getSingle())
                        .put("index", String.valueOf(index))
                        .put("stat", String.valueOf((int) data.getStat("none", stat))))));
    return Result.of(builder.toString());
  }
}
