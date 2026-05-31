package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.arguments.ArgumentBehaviour;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.PGMMatchHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import net.dv8tion.jda.api.entities.Member;

/** Commands related to matches */
public class MatchCommands {

  @GuidoJdaPermission("guido.match")
  @Command(aliases = "match", description = "match.desc")
  public Result match(
      CommandContext context,
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "match.ladder", description = "match.ladder.desc") Ladder ladder,
      @Required(
              name = "match.participants",
              description = "match.participants.desc",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          Linkable[] participants) {
    if (participants.length == 0) {
      return Result.of(locale.get("match.mention-one"));
    } else if (ladder.playersPerTeam() != participants.length / 2) {
      return Result.of(
          locale.get(
              "match.different-than-required",
              Maps.builder("given", String.valueOf(participants.length))
                  .put("expected", String.valueOf(ladder.baseValue() * 2))));
    }
    Set<TeamMember> members1 = new HashSet<>();
    Set<TeamMember> members2 = new HashSet<>();
    for (int i = 0; i < participants.length; i++) {
      Linkable participant = participants[i];
      if (i > ladder.playersPerTeam() - 1) {
        members2.add(new TeamMember(participant.getInfo(), TeamRole.NORMAL));
      } else {
        members1.add(new TeamMember(participant.getInfo(), TeamRole.NORMAL));
      }
    }
    MatchTeam team1 = new MatchTeam(1, members1, "Team 1");
    MatchTeam team2 = new MatchTeam(2, members2, "Team 2");
    Map<String, Map<String, Object>> information = new HashMap<>();
    information.put(
        "global",
        Maps.objects("manual", true)
            .put("ladder", ladder.getName())
            .put("guild", guild.getId())
            .build());
    AbstractMatch match =
        new AbstractMatch(information, Lots.set(team1, team2), MatchStatus.WAITING, null).cache();
    if (context.hasFlag("-t")) {
      match.finish(null);
    } else {
      match.finish(team1);
    }
    return Result.of(locale.get("match.saved", Maps.singleton("id", match.getId())));
  }

  @GuidoJdaPermission("guido.match.void")
  @Command(aliases = "void", description = "void.desc")
  public Result voidMatch(
      LocaleFile locale,
      @Required(name = "void.match", description = "void.match.desc") AbstractMatch match) {
    if (match.getStatus() == MatchStatus.VOIDED) return Result.of(locale.get("void.already"));
    Guido.getHandlers().getHandler(MatchEloCalculator.class).voidMatch(match, true);
    Guido.getHandlers().getHandler(RanksHandler.class).update(match, false);
    return Result.of(locale.get("void.voided"));
  }

  @GuidoJdaPermission("guido.match.recount")
  @Command(aliases = "recountAll", description = "recountAll.desc")
  public Result recountAll() {
    MatchEloCalculator calculator = Guido.getHandlers().getHandler(MatchEloCalculator.class);
    Collection<MatchInfo> matches =
        Guido.getHandlers().getLoader().getMatches().getMatches(-1, -1, MatchStatus.FINISHED);
    for (MatchInfo matchInfo : matches) {
      AbstractMatch match = matchInfo.toMatch();
      if (match == null) continue;
      calculator.recount(match, false);
    }
    return Result.of("All matches have been recounted");
  }

  // TODO probably must go on another class
  @Command(aliases = "updateRanks", description = "Updates the ranks of all the members in a guild")
  public Result updateRanks(GuidoGuild guild) {
    RanksHandler ranksHandler = Guido.getHandlers().getHandler(RanksHandler.class);
    for (LinkableInfo link :
        Guido.getHandlers().getLoader().getLinks().getLinks(-1, -1, LinkableType.MINECRAFT)) {
      Linkable data = link.getLink();
      if (data == null) continue;
      ranksHandler.update(data, guild);
    }
    return Result.of("Guild ranks have been updated");
  }

  // TODO localize
  @GuidoJdaPermission("guido.match.recount")
  @Command(aliases = "recount", description = "Recount the elo of a match")
  public Result recount(
      @Required(name = "AbstractMatch", description = "The match to void") AbstractMatch match) {
    Guido.getHandlers().getHandler(MatchEloCalculator.class).recount(match, false);
    Guido.getHandlers().getHandler(RanksHandler.class).update(match, false);
    return Result.of("AbstractMatch has been recounted");
  }

  @Command(
      aliases = {"game", "gameInfo", "gi", "matchInfo", "mi"},
      description = "game.desc")
  public Result game(
      LocaleFile locale, @Required(name = "game.id", description = "game.id.desc") String id) {
    AbstractMatch match = Guido.getHandlers().getLoader().getMatches().getMatch(id);
    if (match != null) {
      // return Result.of(Matches.getInformation(match, locale));
      return null; // TODO
    } else {
      return Result.of(locale.get("game.not-found", Maps.singleton("id", id)));
    }
  }

  @GuidoJdaPermission("guido.finish")
  @Command(aliases = "finish", description = "finish.desc")
  public Result finish(
      LocaleFile locale,
      GuidoGuild guild,
      CommandContext context,
      @Required(name = "finish.match", description = "finish.match.desc") AbstractMatch match,
      @Free(
              name = "finish.winners",
              description = "finish.winners.desc",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          String name) {
    Map<String, @NonNull String> placeholders = Maps.singleton("id", match.getId());
    if (match.getGuildId() == guild.getId()) {
      if (match.getStatus() == MatchStatus.FINISHED) {
        return Result.of(locale.get("finish.already", placeholders));
      } else {
        if (name != null) {
          if (context.hasFlag("-t")) {
            match.finish(null);
            return Result.of(locale.get("finish.finished", placeholders));
          }
          MatchTeam matchTeam = match.getTeam(name);
          if (matchTeam != null) {
            match.finish(matchTeam);
            return Result.of(locale.get("finish.finished", placeholders));
          } else {
            placeholders.put("matchTeam", name);
            return Result.of(locale.get("finish.invalid-team", placeholders));
          }
        } else {
          match.finish(null);
          return Result.of(locale.get("finish.finished", placeholders));
        }
      }
    }
    return Result.of(locale.get("finish.invalid-guild", placeholders));
  }

  @GuidoJdaPermission("guido.currently")
  @Command(aliases = "currently", description = "currently.desc")
  public Result currently(
      LocaleFile locale,
      UserData sender,
      @Free(name = "currently.else", description = "currently.else.desc") Member member) {
    MatchMakingHandler handler = Guido.getHandlers().getHandler(MatchMakingHandler.class);
    String single = sender.getId();
    Collection<AbstractMatch> playing;
    List<String> matchesId = new ArrayList<>();
    if (member != null) {
      // member.getIdLong(), member.getGuild().getIdLong()
      Linkable link =
          Guido.getHandlers()
              .getLoader()
              .getLinks()
              .getLink(LinkableType.DISCORD, Maps.singleton("id", member.getIdLong()));
      if (link != null) {
        UserData linkedUser = link.getLinkedUser();
        if (linkedUser != null) {
          playing = handler.getPlaying(linkedUser);
          single = link.getSingle();
        } else {
          playing = handler.getPlaying(sender);
        }
      } else {
        playing = new ArrayList<>();
      }
    } else {
      playing = handler.getPlaying(sender);
    }
    for (AbstractMatch match : playing) {
      matchesId.add(match.getId());
    }
    if (playing.isEmpty()) {
      return Result.of(locale.get("currently.empty", Maps.singleton("single", single)));
    } else {
      return Result.of(
          locale.get(
              "currently.success",
              Maps.builder("single", single).put("matches", Lots.pretty(matchesId))));
    }
  }

  @GuidoJdaPermission("user:guido.look")
  @Command(aliases = "look", description = "Makes the active PGM matches look for servers")
  public void look() {
    Guido.getHandlers().getHandler(PGMMatchHandler.class).lookForServers();
  }

  @GuidoJdaPermission("guido.win")
  @Command(aliases = "win", description = "win.desc")
  public Result win(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "win.ladder", description = "win.ladder.desc") Ladder ladder,
      @Required(
              name = "win.entities",
              description = "win.entities.desc",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          Linkable[] linkables) {
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("win.cannot-global"));
    if (linkables.length == 0) return Result.of(locale.get("win.mention-one"));
    MatchEloCalculator eloHandler = Guido.getHandlers().getHandler(MatchEloCalculator.class);
    RanksHandler ranksHandler = Guido.getHandlers().getHandler(RanksHandler.class);
    for (Linkable linkable : linkables) {
      eloHandler.setElo(linkable, true, ladder, true);
      ranksHandler.update(linkable, guild);
    }
    return Result.of(locale.get("win.updated"));
  }

  @GuidoJdaPermission("guido.lose")
  @Command(aliases = "lose", description = "lose.desc")
  public Result lose(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "lose.ladder", description = "lose.ladder.desc") Ladder ladder,
      @Required(name = "lose.entities", description = "lose.entities.desc") Linkable[] linkables) {
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("lose.cannot-global"));
    if (linkables.length == 0) return Result.of(locale.get("lose.mention-one"));
    MatchEloCalculator handler = Guido.getHandlers().getHandler(MatchEloCalculator.class);
    RanksHandler ranksHandler = Guido.getHandlers().getHandler(RanksHandler.class);
    for (Linkable linkable : linkables) {
      handler.setElo(linkable, false, ladder, true);
      ranksHandler.update(linkable, guild);
    }
    return Result.of(locale.get("lose.updated"));
  }
}
