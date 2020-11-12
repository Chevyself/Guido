package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.api.user.UserData;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.bot.core.types.GuidoTeam;
import me.googas.bot.core.types.GuidoTeamMember;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

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
    Set<TeamMember> members1 = new HashSet<>();
    Set<TeamMember> members2 = new HashSet<>();
    for (int i = 0; i < message.getMentionedMembers().size(); i++) {
      Member mentioned = message.getMentionedMembers().get(i);
      BotLinkableData member =
          Guido.getDataLoader()
              .getMemberData(mentioned.getIdLong(), mentioned.getGuild().getIdLong());
      if (i > ladder.playersPerTeam() - 1) {
        members2.add(new GuidoTeamMember(member.getInfo(), TeamRole.NORMAL));
      } else {
        members1.add(new GuidoTeamMember(member.getInfo(), TeamRole.NORMAL));
      }
    }
    GuidoTeam team1 = new GuidoTeam(1, members1, "Team 1");
    GuidoTeam team2 = new GuidoTeam(2, members2, "Team 2");
    GuidoMatch match =
        new GuidoMatch(
                guild.getId(),
                Lots.set(team1, team2),
                new GuidoLinkedValuesMap("manual", true)
                    .put("ladder", ladder.getName())
                    .put("guild", guild.getId()))
            .cache();
    if (context.hasFlag("-t")) {
      match.finish(null);
    } else {
      match.finish(team1);
    }
    return new Result(locale.get("match.saved", Maps.singleton("id", match.getId())));
  }

  /**
   * See the information about a match
   *
   * @param locale the locale of the user that will see the match
   * @param id the id of the match
   * @return the result of the command
   */
  @Command(
      aliases = {"game", "gameinfo", "gi", "matchinfo", "mi"},
      description = "game.desc")
  public Result game(
      LocaleFile locale, @Required(name = "game.id", description = "game.id.desc") String id) {
    BotMatch match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      return new Result(match.getInformation(locale));
    } else {
      return new Result(locale.get("game.not-found", Maps.singleton("id", id)));
    }
  }

  /**
   * Finishes the given match
   *
   * @param locale the locale of the command sender
   * @param guild the guild where the match is being finished
   * @param match the match being finished
   * @param name the name of the team to select as winners
   * @return whether the match was finished
   */
  @Command(
      aliases = "finish",
      description = "finish.desc",
      permission = @Perm(node = "guido.finish"))
  public Result finish(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "finish.match", description = "finish.match.desc") Match match,
      @Optional(name = "finish.winners", description = "finish.winners.desc") JoinedStrings name) {
    Map<String, @NotNull String> placeholders = Maps.singleton("id", match.getId());
    if (match.getGuildId() == guild.getId()) {
      if (match.getStatus() == MatchStatus.FINISHED) {
        return new Result(ResultType.USAGE, locale.get("finish.already", placeholders));
      } else {
        if (name != null) {
          Team team = match.getTeam(name.getString());
          if (team != null) {
            match.finish(team);
            return new Result(locale.get("finish.finished", placeholders));
          } else {
            placeholders.put("team", name.getString());
            return new Result(locale.get("finish.invalid-team", placeholders));
          }
        } else {
          match.finish(null);
          return new Result(locale.get("finish.finished", placeholders));
        }
      }
    }
    return new Result(ResultType.USAGE, locale.get("finish.invalid-guild", placeholders));
  }

  /**
   * Get where someone is playing
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @param member the member to check where it is playing
   * @return the matches where the query is playing
   */
  @Command(
      aliases = "currently",
      description = "currently.desc",
      permission = @Perm(node = "guido.currently"))
  public Result currently(
      LocaleFile locale,
      UserData sender,
      @Optional(name = "currently.else", description = "currently.else.desc") Member member) {
    MatchMakingHandler handler = Guido.getHandler(MatchMakingHandler.class);
    String single = sender.getId();
    Collection<Match> playing;
    List<String> matchesId = new ArrayList<>();
    if (member != null) {
      BotLinkableData link =
          Guido.getDataLoader().getMemberData(member.getIdLong(), member.getGuild().getIdLong());
      UserData linkedUser = link.getLinkedUser();
      if (linkedUser != null) {
        playing = handler.getPlaying(linkedUser);
        single = link.getSingle();
      } else {
        playing = handler.getPlaying(sender);
      }
    } else {
      playing = handler.getPlaying(sender);
    }
    for (Match match : playing) {
      matchesId.add(match.getId());
    }
    if (playing.isEmpty()) {
      return new Result(locale.get("currently.empty", Maps.singleton("single", single)));
    } else {
      return new Result(
          locale.get(
              "currently.success",
              Maps.builder("single", single).append("matches", Lots.pretty(matchesId))));
    }
  }
}
