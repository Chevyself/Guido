package me.googas.bot.core.commands;

import com.starfishst.commands.jda.annotations.Command;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.commands.jda.result.ResultType;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Role;

/** Commands for ranges */
public class RangesCommand {

  /**
   * See the range of certain role
   *
   * @param locale the locale to getId
   * @param guild the guild to getId the range from
   * @param role the role to getId the range
   * @return the range
   */
  @Parent
  @Command(aliases = "range", description = "range.desc", node = "guido.range")
  public Result range(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "range.role", description = "range.role.desc") Role role) {
    RankRange range = guild.getRange(role.getIdLong());
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
  @Command(aliases = "set", description = "range.set.desc", node = "guido.range.set")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "range.set.role", description = "range.set.role.desc") Role role,
      @Required(name = "range.set.ladder", description = "range.set.ladder.desc") Ladder ladder,
      @Required(name = "range.set.min", description = "range.set.min.desc") int min,
      @Required(name = "range.set.max", description = "range.set.max.desc") int max) {
    RankRange range =
        new RankRange(
            ladder.getName(),
            Maps.singleton(
                "global",
                Maps.objects("id", role.getIdLong()).append("name", role.getName()).build()),
            min,
            max);
    guild.getRanges().add(range);
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
      node = "guido.range.delete")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "range.del.role", description = "range.del.role.desc") Role role) {
    RankRange range = guild.getRange(role.getIdLong());
    if (range != null) {
      guild.getRanges().remove(range);
      return new Result(
          locale.get("range.del.success", Maps.singleton("mention", role.getAsMention())));
    } else {
      return new Result(
          locale.get("range.del.not-found", Maps.singleton("mention", role.getAsMention())));
    }
  }
}
