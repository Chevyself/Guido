package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.api.utility.Maps;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.discord.GuidoGuild;
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
  @GuidoJdaPermission("guido.range")
  @Command(aliases = "range", description = "range.desc")
  public Result range(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "range.role", description = "range.role.desc") Role role) {
    RankRange range = guild.getRange(role.getIdLong());
    if (range != null) {
      return Result.of(
          locale.get(
              "range.range",
              Maps.builder("mention", role.getAsMention())
                  .put("ladder", range.getLadder())
                  .put("min", String.valueOf(range.getMin()))
                  .put("max", String.valueOf(range.getMax()))));
    } else {
      return Result.of(
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
  @GuidoJdaPermission("guido.range.set")
  @Command(aliases = "set", description = "range.set.desc")
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
                "global", Maps.objects("id", role.getIdLong()).put("name", role.getName()).build()),
            min,
            max);
    guild.getRanges().add(range);
    return Result.of(
        locale.get(
            "range.set.success",
            Maps.builder("mention", role.getAsMention())
                .put("ladder", range.getLadder())
                .put("min", String.valueOf(range.getMin()))
                .put("max", String.valueOf(range.getMax()))));
  }

  /**
   * Delete the range for certain role
   *
   * @param locale the locale of the command sender
   * @param guild the guild to delete the range from
   * @param role the role to remove the range from
   * @return the result of the command
   */
  @GuidoJdaPermission("guido.range.delete")
  @Command(
      aliases = {"delete", "remove", "del"},
      description = "range.del.desc")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "range.del.role", description = "range.del.role.desc") Role role) {
    RankRange range = guild.getRange(role.getIdLong());
    if (range != null) {
      guild.getRanges().remove(range);
      return Result.of(
          locale.get("range.del.success", Maps.singleton("mention", role.getAsMention())));
    } else {
      return Result.of(
          locale.get("range.del.not-found", Maps.singleton("mention", role.getAsMention())));
    }
  }
}
