package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.googas.commons.Pagination;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;

/** Commands for editing multipliers in guilds */
public class MultiplierCommands {

  /**
   * See all the values of a guild
   *
   * @param locale the locale of the command sender
   * @param guild the guild to see the multipliers from
   * @param page the page to see of multipliers
   * @return the multipliers inside the guild
   */
  @Parent
  @Command(
      aliases = "multipliers",
      description = "multipliers.desc",
      permission = @Perm(node = "guido.multipliers"))
  public Result multipliers(
      LocaleFile locale,
      BotGuild guild,
      @Optional(name = "multipliers.page", description = "multipliers.page.desc", suggestions = "1")
          int page) {
    if (guild.getMultipliers().isEmpty()) {
      return new Result(locale.get("multipliers.empty"));
    } else {
      Pagination<String> pagination =
          new Pagination<>(new ArrayList<>(guild.getMultipliers().keySet()));
      if (page < 1) {
        page = 1;
      } else if (page > pagination.maxPage()) {
        page = pagination.maxPage();
      }
      List<String> toSee = pagination.getPage(page);
      StringBuilder builder = Strings.getBuilder();
      builder.append(
          locale.get(
              "multipliers.title",
              Maps.builder("page", String.valueOf(page))
                  .append("max", String.valueOf(pagination.maxPage()))));
      for (String key : toSee) {
        builder.append(
            locale.get(
                "multipliers.multiplier",
                Maps.builder("name", key)
                    .append("value", String.valueOf(guild.getMultipliers().get(key)))));
      }
      return new Result(builder.toString());
    }
  }

  /**
   * Set the value for certain multiplier
   *
   * @param locale the locale of the command sender
   * @param guild the guild to set the multiplier value to
   * @param name the name of the multiplier
   * @param value the value of the multiplier
   * @return that the value has been set
   */
  @Command(
      aliases = "set",
      description = "multi.set.desc",
      permission = @Perm(node = "guido.multipliers.set"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "multi.set.name", description = "multi.set.name.desc") String name,
      @Required(name = "multi.set.value", description = "multi.set.value.desc") int value) {
    guild.getMultipliers().put(name, value);
    return new Result(
        locale.get(
            "multi.set.success",
            Maps.builder("name", name).append("value", String.valueOf(value))));
  }

  /**
   * Delete a multiplier with the given name
   *
   * @param locale the locale of the command sender
   * @param guild the guild to change
   * @param name the name of the multiplier to delete
   * @return whether the multiplier was deleted
   */
  @Command(
      aliases = "delete",
      description = "multi.delete.desc",
      permission = @Perm(node = "guido.delete.desc"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "multi.delete.name", description = "multi.delete.name.desc") String name) {
    HashMap<String, String> placeholders = Maps.singleton("name", name);
    if (guild.getMultipliers().containsKey(name)) {
      guild.getMultipliers().remove(name);
      return new Result(locale.get("multi.delete.success", placeholders));
    } else {
      return new Result(locale.get("multi.delete.not-exists", placeholders));
    }
  }
}
