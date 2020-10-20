package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.handlers.data.GuidoLadder;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;

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
      description = "ladders.desc",
      permission = @Perm(node = "guido.ladders"))
  public Result ladders(
      LocaleFile locale,
      BotGuild guild,
      @Optional(name = "ladders.page", description = "ladders.page.desc", suggestions = "1")
          int page) {
    if (guild.getLadders().isEmpty()) {
      return new Result(locale.get("ladders.empty"));
    } else {
      Pagination<Ladder> pagination = new Pagination<>(new ArrayList<>(guild.getLadders()), 10);
      if (page < 1) {
        page = 1;
      } else if (page > pagination.maxPage()) {
        page = pagination.maxPage();
      }
      List<Ladder> ladders = pagination.getPage(page);
      StringBuilder builder = Strings.getBuilder();
      builder.append(
          locale.get(
              "ladders.title",
              Maps.builder("page", String.valueOf(page))
                  .append("max", String.valueOf(pagination.maxPage()))));
      for (Ladder ladder : ladders) {
        builder.append(
            locale.get(
                "ladders.ladder",
                Maps.builder("name", ladder.getName())
                    .append("base", String.valueOf(ladder.baseValue()))
                    .append("players", String.valueOf(ladder.playersPerTeam()))));
      }
      return new Result(builder.toString());
    }
  }

  /**
   * Create a new ladder
   *
   * @param locale the locale of the command sender
   * @param guild the guild to edit
   * @param name the name of the ladder
   * @param players the max players in the ladder
   * @param base the base value which players start with
   * @return whether the ladders was created
   */
  @Command(
      aliases = {"create", "make"},
      description = "ladders.make.desc",
      permission = @Perm(node = "guido.ladders.make"))
  public Result create(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "ladders.make.name", description = "ladders.make.name.desc") String name,
      @Required(name = "ladders.make.players", description = "ladders.make.players.desc")
          int players,
      @Required(name = "ladders.make.base", description = "ladders.make.base.desc") int base) {
    MapBuilder<String, String> placeholders =
        Maps.builder("name", name)
            .append("players", String.valueOf(players))
            .append("base", String.valueOf(base));
    if (guild.getLadder(name) != null) {
      return new Result(ResultType.USAGE, locale.get("ladders.make.exists", placeholders));
    } else {
      guild.getLadders().add(new GuidoLadder(name, players, base));
      return new Result(locale.get("ladders.make.success", placeholders));
    }
  }

  /**
   * Delete a ladder
   *
   * @param locale the locale of the command sender
   * @param guild the guild to edit
   * @param name the name of the ladder to delete
   * @return whether the ladder was deleted
   */
  @Command(
      aliases = {"delete", "del"},
      description = "ladders.del.desc",
      permission = @Perm(node = "guido.ladders.del"))
  public Result dedlete(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "ladders.del.name", description = "ladders.del.name.desc") String name) {
    HashMap<String, String> placeholder = Maps.singleton("name", name);
    if (guild.getLadder(name) == null) {
      return new Result(locale.get("ladders.del.not-exists", placeholder));
    } else {
      guild.getLadders().removeIf(ladder -> ladder.getName().equalsIgnoreCase(name));
      return new Result(locale.get("ladders.del.success", placeholder));
    }
  }
}
