package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.handlers.data.GuidoMatch;
import com.starfishst.bot.handlers.data.GuidoTeam;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.TeamRole;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.HashMap;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

/** Commands related to matches */
public class MatchCommands {

  /**
   * Save a match
   *
   * @param context the context of the command
   * @param locale the locale of the person reading the result
   * @param message the message to get the mentioned users
   * @param guild the guild where the command is being executed
   * @param ladder the ladder in which this match happened
   * @return the result of the command execution
   */
  @Command(aliases = "match", description = "match.desc", permission = @Perm(node = "guido.match"))
  public Result match(
      CommandContext context,
      LocaleFile locale,
      Message message,
      BotGuild guild,
      @Required(name = "match.ladder", description = "match.ladder.desc") Ladder ladder) {
    if (message.getMentionedMembers().isEmpty()) {
      return new Result(ResultType.USAGE, locale.get("match.mention-one"));
    } else if (ladder.playersPerTeam() != message.getMentionedMembers().size() / 2) {
      return new Result(
          ResultType.USAGE,
          locale.get(
              "match.different-than-required",
              Maps.builder("given", String.valueOf(message.getMentionedMembers().size()))
                  .append("expected", String.valueOf(ladder.baseValue() * 2))));
    }
    HashMap<BotLinkedInfo, TeamRole> members1 = new HashMap<>();
    HashMap<BotLinkedInfo, TeamRole> members2 = new HashMap<>();
    for (int i = 0; i < message.getMentionedMembers().size(); i++) {
      Member mentioned = message.getMentionedMembers().get(i);
      BotLinkedData member =
          Guido.getDataLoader()
              .getMemberData(mentioned.getIdLong(), mentioned.getGuild().getIdLong());
      if (i > ladder.playersPerTeam() - 1) {
        members2.put(member.getInfo(), TeamRole.NORMAL);
      } else {
        members1.put(member.getInfo(), TeamRole.NORMAL);
      }
    }
    GuidoTeam team1 = new GuidoTeam(members1);
    GuidoTeam team2 = new GuidoTeam(members2);
    GuidoMatch match =
        new GuidoMatch(
            Guido.getDataLoader().nextMatchId(),
            Lots.set(team1, team2),
            new GuidoValuesMap("manual", true).addValue("ladder", ladder.getName()));
    if (context.hasFlag("-t")) {
      match.finish(null);
    } else {
      match.finish(team1);
    }
    return new Result(locale.get("match.saved", Maps.singleton("id", match.getId())));
  }
}
