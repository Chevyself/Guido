package me.googas.bot.commands;

import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import me.googas.api.RankRange;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Ladder;
import me.googas.bot.api.data.BotGuild;
import me.googas.bot.handlers.data.types.GuidoRankRange;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Role;

/** Commands for ranges */
public class RangesCommand {

  /**
   * See the range of certain role
   *
   * @param locale the locale to get
   * @param guild the guild to get the range from
   * @param role the role to get the range
   * @return the range
   */
  @Parent
  @Command(aliases = "range", description = "range.desc", permission = @Perm(node = "guido.range"))
  public Result range(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "range.role", description = "range.role.desc") Role role) {
    RankRange range = guild.getRanges().get(role.getIdLong());
    if (range != null) {
      return new Result(
          locale.get(
              "range.range",
              Maps.builder("mention", role.getAsMention())
                  .append("ladder", range.getLadder())
                  .append("min", String.valueOf(range.getMin()))
                  .append("max", String.valueOf(range.getMax()))));
    } else {
      return new Result(
          ResultType.USAGE,
          locale.get("range.no-range", Maps.singleton("mention", role.getAsMention())));
    }
  }

  /**
   * Set the range for certain role
   *
   * @param locale the locale of the sender
   * @param guild the guild to set the range in
   * @param role the role to set the range
   * @param ladder the ladder that will be applied for the range
   * @param min the minimum value of the range
   * @param max the maximum value of the range
   * @return the result saying that the range was set
   */
  @Command(
      aliases = "set",
      description = "range.set.desc",
      permission = @Perm(node = "guido.range.set"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "range.set.role", description = "range.set.role.desc") Role role,
      @Required(name = "range.set.ladder", description = "range.set.ladder.desc") Ladder ladder,
      @Required(name = "range.set.min", description = "range.set.min.desc") int min,
      @Required(name = "range.set.max", description = "range.set.max.desc") int max) {
    GuidoRankRange range = new GuidoRankRange(ladder.getName(), min, max);
    guild.getRanges().put(role.getIdLong(), range);
    return new Result(
        locale.get(
            "range.set.success",
            Maps.builder("mention", role.getAsMention())
                .append("ladder", range.getLadder())
                .append("min", String.valueOf(range.getMin()))
                .append("max", String.valueOf(range.getMax()))));
  }

  /**
   * Delete the range for certain role
   *
   * @param locale the locale of the command sender
   * @param guild the guild to delete the range from
   * @param role the role to remove the range from
   * @return the result of the command
   */
  @Command(
      aliases = {"delete", "remove", "del"},
      description = "range.del.desc",
      permission = @Perm(node = "guido.range.delete"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "range.del.role", description = "range.del.role.desc") Role role) {
    if (guild.getRanges().containsKey(role.getIdLong())) {
      guild.getRanges().remove(role.getIdLong());
      return new Result(
          locale.get("range.del.success", Maps.singleton("mention", role.getAsMention())));
    } else {
      return new Result(
          locale.get("range.del.not-found", Maps.singleton("mention", role.getAsMention())));
    }
  }
}
