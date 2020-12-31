package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.PGMMatchHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.matches.GuidoMatchTeam;
import me.googas.bot.core.matches.team.GuidoTeamMember;
import me.googas.bot.core.util.Matches;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Member;

/** Commands related to matches */
public class MatchCommands {

  @Command(aliases = "match", description = "match.desc", node = "guido.match")
  public Result match(
      CommandContext context,
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "match.ladder", description = "match.ladder.desc") Ladder ladder,
      @Multiple @Required(name = "match.participants", description = "match.participants.desc")
          Linkable[] participants) {
    if (participants.length == 0) {
      return new Result(ResultType.USAGE, locale.get("match.mention-one"));
    } else if (ladder.playersPerTeam() != participants.length / 2) {
      return new Result(
          ResultType.USAGE,
          locale.get(
              "match.different-than-required",
              Maps.builder("given", String.valueOf(participants.length))
                  .append("expected", String.valueOf(ladder.baseValue() * 2))));
    }
    Set<TeamMember> members1 = new HashSet<>();
    Set<TeamMember> members2 = new HashSet<>();
    for (int i = 0; i < participants.length; i++) {
      Linkable participant = participants[i];
      if (i > ladder.playersPerTeam() - 1) {
        members2.add(new GuidoTeamMember(participant.getInfo(), TeamRole.NORMAL));
      } else {
        members1.add(new GuidoTeamMember(participant.getInfo(), TeamRole.NORMAL));
      }
    }
    GuidoMatchTeam team1 = new GuidoMatchTeam(1, members1, "Team 1");
    GuidoMatchTeam team2 = new GuidoMatchTeam(2, members2, "Team 2");
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

  // TODO localize
  @Command(aliases = "void", description = "Voids a match", node = "guido.match.void")
  public Result voidMatch(
      @Required(name = "Match", description = "The match to void") Match match) {
    if (match.getStatus() == MatchStatus.VOIDED)
      return new Result(ResultType.ERROR, "Match has been voided already");
    Guido.getHandler(MatchEloCalculator.class).voidMatch(match, true);
    Guido.getHandler(RanksHandler.class).update(match);
    return new Result("Match has been voided");
  }

  @Command(
      aliases = "recount",
      description = "Recount the elo of a match",
      node = "guido.match.recount")
  public Result recount(@Required(name = "Match", description = "The match to void") Match match) {
    Guido.getHandler(MatchEloCalculator.class).recount(match);
    Guido.getHandler(RanksHandler.class).update(match);
    return new Result("Match has been recounted");
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
    Match match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      return new Result(Matches.getInformation(match, locale));
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
  @Command(aliases = "finish", description = "finish.desc", node = "guido.finish")
  public Result finish(
      LocaleFile locale,
      BotGuild guild,
      @Required(name = "finish.match", description = "finish.match.desc") Match match,
      @Multiple @Optional(name = "finish.winners", description = "finish.winners.desc")
          JoinedStrings name) {
    Map<String, @NonNull String> placeholders = Maps.singleton("id", match.getId());
    if (match.getGuildId() == guild.getId()) {
      if (match.getStatus() == MatchStatus.FINISHED) {
        return new Result(ResultType.USAGE, locale.get("finish.already", placeholders));
      } else {
        if (name != null) {
          MatchTeam matchTeam = match.getTeam(name.getString());
          if (matchTeam != null) {
            match.finish(matchTeam);
            return new Result(locale.get("finish.finished", placeholders));
          } else {
            placeholders.put("matchTeam", name.getString());
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
  @Command(aliases = "currently", description = "currently.desc", node = "guido.currently")
  public Result currently(
      LocaleFile locale,
      UserData sender,
      @Optional(name = "currently.else", description = "currently.else.desc") Member member) {
    MatchMakingHandler handler = Guido.getHandler(MatchMakingHandler.class);
    String single = sender.getId();
    Collection<Match> playing;
    List<String> matchesId = new ArrayList<>();
    if (member != null) {
      // member.getIdLong(), member.getGuild().getIdLong()
      Linkable link =
          Guido.getDataLoader()
              .getLink(LinkableType.DISCORD, new GuidoValuesMap("id", member.getIdLong()));
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

  @Command(
      aliases = "look",
      description = "Makes the active PGM matches look for servers",
      node = "user:guido.look")
  public void look() {
    Guido.getHandler(PGMMatchHandler.class).lookForServers();
  }
}
