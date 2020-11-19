package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Ladder;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.core.types.GuidoLadder;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.Lots;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.maps.MapBuilder;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
      guild.getLadders().add(new GuidoLadder(name, players, base, 2, new GuidoValuesMap()));
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
  public Result delete(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "ladders.del.name", description = "ladders.del.name.desc") String name) {
    Map<String, String> placeholder = Maps.singleton("name", name);
    if (guild.getLadder(name) == null) {
      return new Result(locale.get("ladders.del.not-exists", placeholder));
    } else {
      guild.getLadders().removeIf(ladder -> ladder.getName().equalsIgnoreCase(name));
      return new Result(locale.get("ladders.del.success", placeholder));
    }
  }

  /**
   * @see #setValue(LocaleFile, Ladder, String, Object). This sets string values
   * @param locale the locale of the command sender
   * @param ladder the ladder to edit
   * @param key the key of the new value
   * @param value the new value
   * @return whether the value was set
   */
  @Command(
      aliases = "string",
      description = "ladder.edit.string",
      permission = @Perm(node = "guido.ladders.edit"))
  public Result string(
      LocaleFile locale,
      @Required(name = "ladder.edit.ladder", description = "ladder.edit.ladder.desc") Ladder ladder,
      @Optional(name = "ladder.edit.key", description = "ladder.edit.key.desc") String key,
      @Required(name = "ladder.edit.value", description = "ladder.edit.value.desc") String value) {
    return this.setValue(locale, ladder, key, value);
  }

  /**
   * @see #setValue(LocaleFile, Ladder, String, Object). This sets integer values
   * @param locale the locale of the command sender
   * @param ladder the ladder to edit
   * @param key the key of the new value
   * @param value the new value
   * @return whether the value was set
   */
  @Command(
      aliases = "integer",
      description = "ladder.edit.integer",
      permission = @Perm(node = "guido.ladders.edit"))
  public Result integer(
      LocaleFile locale,
      @Required(name = "ladder.edit.ladder", description = "ladder.edit.ladder.desc") Ladder ladder,
      @Optional(name = "ladder.edit.key", description = "ladder.edit.key.desc") String key,
      @Required(name = "ladder.edit.value", description = "ladder.edit.value.desc") int value) {
    return this.setValue(locale, ladder, key, value);
  }

  /**
   * Set the value of an option in a ladder
   *
   * @param locale the locale of the command sender
   * @param ladder the ladder to edit
   * @param key the key of the new value
   * @param value the new value
   * @return whether the value was set
   */
  private Result setValue(
      @NotNull LocaleFile locale,
      @NotNull Ladder ladder,
      @NotNull String key,
      @Nullable Object value) {
    if (value == null && ladder.getOptions().getMap().containsKey(key)) {
      ladder.getOptions().remove(key);
    } else if (value != null) {
      ladder.getOptions().put(key, value);
    }
    String valueString;
    if (value != null) {
      if (value instanceof Collection) {
        valueString = Lots.pretty((Collection<?>) value);
      } else {
        valueString = value.toString();
      }
    } else {
      valueString = "null";
    }
    return new Result(
        locale.get("ladder.edit.result", Maps.builder("key", key).append("value", valueString)));
  }
}
