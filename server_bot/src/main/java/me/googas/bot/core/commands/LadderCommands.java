package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.util.List;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.server.GuidoGuild;
import me.googas.starbox.Pagination;
import me.googas.starbox.builders.MapBuilder;

/** Commands for ladders */
public class LadderCommands {

  /**
   * See the ladders inside a guild
   *
   * @param locale the locale of the sender
   * @param guild the guild to see the ladders from
   * @param page the page to see of ladders
   * @return the ladders
   */
  @Parent
  @Command(
      aliases = {"ladders", "ladder"},
      description = "ladders.desc")
  public Result ladders(
      LocaleFile locale,
      GuidoGuild guild,
      @Free(name = "ladders.page", description = "ladders.page.desc", suggestions = "1") int page) {
    if (guild.getLadders().isEmpty()) {
      return Result.of(locale.get("ladders.empty"));
    } else {
      Pagination<? extends Ladder> pagination = Lots.pagesOf(guild.getLadders(), 10);
      if (page < 1) {
        page = 1;
      } else if (page > pagination.maxPage()) {
        page = pagination.maxPage();
      }
      List<? extends Ladder> ladders = pagination.getPage(page);
      StringBuilder builder = new StringBuilder();
      builder.append(
          locale.get(
              "ladders.title",
              Maps.builder("page", String.valueOf(page))
                  .put("max", String.valueOf(pagination.maxPage()))));
      for (Ladder ladder : ladders) {
        builder.append(
            locale.get(
                "ladders.ladder",
                Maps.builder("name", ladder.getName())
                    .put("base", String.valueOf(ladder.baseValue()))
                    .put("players", String.valueOf(ladder.playersPerTeam()))));
      }
      return Result.of(builder.toString());
    }
  }

  @GuidoJdaPermission("guido.ladders.make")
  @Command(
      aliases = {"create", "make"},
      description = "ladders.make.desc")
  public Result create(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "ladders.make.name", description = "ladders.make.name.desc") String name,
      @Required(name = "ladders.make.players", description = "ladders.make.players.desc")
          int players,
      @Required(name = "ladders.make.base", description = "ladders.make.base.desc") int base,
      @Required(name = "ladders.make.team", description = "ladders.make.team.desc")
          MinecraftTeamSelectionType selectionType) {
    MapBuilder<String, String> placeholders =
        Maps.builder("name", name)
            .put("players", String.valueOf(players))
            .put("base", String.valueOf(base));

    return guild
        .getLadder(name)
        .map(ladder -> Result.of(locale.get("ladders.make.exists", placeholders)))
        .orElseGet(
            () -> {
              guild.addLadder(name, players, base, 2, 1, 1, selectionType);
              return Result.of(locale.get("ladders.make.success", placeholders));
            });
  }

  @GuidoJdaPermission("guido.ladders.del")
  @Command(
      aliases = {"delete", "del"},
      description = "ladders.del.desc")
  public Result delete(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "ladders.del.name", description = "ladders.del.name.desc") String name) {
    Map<String, String> placeholder = Maps.singleton("name", name);
    return guild
        .getLadder(name)
        .map((ladder) -> Result.of(locale.get("ladders.del.not-exists", placeholder)))
        .orElseGet(
            () -> {
              guild.removeLadderByName(name);
              return Result.of(locale.get("ladders.del.success", placeholder));
            });
  }
}
