package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.arguments.ArgumentBehaviour;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.result.Result;
import java.util.*;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.DiscordLinkable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.MinecraftMatchLoader;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.user.UserData;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoHandlerRegistry;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.commands.types.EmbededResult;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;
import me.googas.bot.core.handlers.matches.MatchMakingHandler;
import me.googas.bot.core.handlers.matches.PGMMatchHandler;
import me.googas.bot.core.handlers.ranks.RanksHandler;
import me.googas.bot.core.matches.queue.ImmutableMinecraftMatchTeam;
import me.googas.bot.core.matches.queue.ImmutableMinecraftTeamMember;
import me.googas.bot.core.util.Matches;
import me.googas.server.GuidoGuild;
import net.dv8tion.jda.api.entities.Member;

/** Commands related to matches */
public class MatchCommands {

  @GuidoJdaPermission("guido.match")
  @Command(aliases = "match", description = "match.desc")
  public Result match(
      CommandContext context,
      LocaleFile locale,
      GuidoGuild guild,
      MinecraftMatchLoader minecraftMatchLoader,
      @Required(name = "match.ladder", description = "match.ladder.desc") Ladder ladder,
      @Required(
              name = "match.participants",
              description = "match.participants.desc",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          MinecraftLinkable[] participants) {
    if (participants.length == 0) {
      return Result.of(locale.get("match.mention-one"));
    } else if (ladder.playersPerTeam() != participants.length / 2) {
      return Result.of(
          locale.get(
              "match.different-than-required",
              Maps.builder("given", String.valueOf(participants.length))
                  .put("expected", String.valueOf(ladder.baseValue() * 2))));
    }
    Set<ImmutableMinecraftTeamMember> members1 = new HashSet<>();
    Set<ImmutableMinecraftTeamMember> members2 = new HashSet<>();
    for (int i = 0; i < participants.length; i++) {
      MinecraftLinkable participant = participants[i];
      ImmutableMinecraftTeamMember member =
          new ImmutableMinecraftTeamMember(participant.getId(), TeamRole.MEMBER);
      if (i > ladder.playersPerTeam() - 1) {
        members2.add(member);
      } else {
        members1.add(member);
      }
    }
    ImmutableMinecraftMatchTeam team1 = new ImmutableMinecraftMatchTeam(1, members1, "Team 1");
    ImmutableMinecraftMatchTeam team2 = new ImmutableMinecraftMatchTeam(2, members2, "Team 2");
    MinecraftMatch match =
        minecraftMatchLoader.createMatch(Lots.set(team1, team2), ladder.getName());
    if (context.hasFlag("-t")) {
      match.finish(-1);
    } else {
      match.finish(team1.getId());
    }
    return Result.of(locale.get("match.saved", Maps.singleton("id", match.getId().toString())));
  }

  @GuidoJdaPermission("guido.match.void")
  @Command(aliases = "void", description = "void.desc")
  public Result voidMatch(
      LocaleFile locale,
      GuidoBotRuntime runtime,
      @Required(name = "void.match", description = "void.match.desc") MinecraftMatch match) {
    if (match.getStatus() == MatchStatus.VOIDED) return Result.of(locale.get("void.already"));
    GuidoHandlerRegistry handlers = runtime.getHandlers();
    handlers.getHandler(MatchEloCalculator.class).voidMatch(match, true);
    handlers.getHandler(RanksHandler.class).update(match, false);
    return Result.of(locale.get("void.voided"));
  }

  // TODO probably must go on another class
  // TODO uncomment if it is ever needed
  /*@Command(aliases = "updateRanks", description = "Updates the ranks of all the members in a guild")
  public Result updateRanks(GuidoGuild guild, GuidoBotRuntime runtime) {
    RanksHandler ranksHandler = runtime.getHandlers().getHandler(RanksHandler.class);
    for (LinkableInfo link :
        Guido.getHandlers().getLoader().getLinks().getLinks(-1, -1, LinkableType.MINECRAFT)) {
      Linkable data = link.getLink();
      if (data == null) continue;
      ranksHandler.update(data, guild);
    }
    return Result.of("Guild ranks have been updated");
  }*/

  // TODO localize
  @GuidoJdaPermission("guido.match.recount")
  @Command(aliases = "recount", description = "Recount the elo of a match")
  public Result recount(
      GuidoBotRuntime runtime,
      @Required(name = "MinecraftMatch", description = "The match to void") MinecraftMatch match) {
    GuidoHandlerRegistry handlers = runtime.getHandlers();
    handlers.getHandler(MatchEloCalculator.class).recount(match, false);
    handlers.getHandler(RanksHandler.class).update(match, false);
    return Result.of("MinecraftMatch has been recounted");
  }

  @Command(
      aliases = {"game", "gameInfo", "gi", "matchInfo", "mi"},
      description = "game.desc")
  public Result game(
      LocaleFile locale,
      GuidoBotRuntime runtime,
      @Required(name = "game.id", description = "game.id.desc") String id) {
    return runtime
        .getLoader()
        .getMinecraftMatches()
        .getByRegexId(id)
        .map(
            match ->
                (Result)
                    new EmbededResult(Matches.getInformation(match, locale, runtime.getLoader())))
        .orElse(Result.of(locale.get("game.not-found", Maps.singleton("id", id))));
  }

  @GuidoJdaPermission("guido.finish")
  @Command(aliases = "finish", description = "finish.desc")
  public Result finish(
      LocaleFile locale,
      GuidoGuild guild,
      CommandContext context,
      @Required(name = "finish.match", description = "finish.match.desc") MinecraftMatch match,
      @Free(
              name = "finish.winners",
              description = "finish.winners.desc",
              behaviour = ArgumentBehaviour.CONTINUOUS)
          String name) {
    Map<String, @NonNull String> placeholders = Maps.singleton("id", match.getId().toString());
    if (match.getStatus() == MatchStatus.FINISHED) {
      return Result.of(locale.get("finish.already", placeholders));
    } else {
      if (name != null) {
        if (context.hasFlag("-t")) {
          match.finish();
          return Result.of(locale.get("finish.finished", placeholders));
        }
        match
            .getTeamByName(name)
            .map(
                matchTeam -> {
                  match.finish(matchTeam);
                  return Result.of(locale.get("finish.finished", placeholders));
                })
            .orElseGet(
                () -> {
                  placeholders.put("matchTeam", name);
                  return Result.of(locale.get("finish.invalid-team", placeholders));
                });
      } else {
        match.finish(-1);
        return Result.of(locale.get("finish.finished", placeholders));
      }
    }
    return Result.of(locale.get("finish.invalid-guild", placeholders));
  }

  @GuidoJdaPermission("guido.currently")
  @Command(aliases = "currently", description = "currently.desc")
  public Result currently(
      LocaleFile locale,
      UserData sender,
      GuidoBotRuntime runtime,
      @Free(name = "currently.else", description = "currently.else.desc") Member member) {
    MatchMakingHandler handler = Guido.getHandlers().getHandler(MatchMakingHandler.class);
    String single = sender.getId().toString();
    Collection<MinecraftMatch> playing;
    List<UUID> matchesId = new ArrayList<>();
    if (member != null) {
      // member.getIdLong(), member.getGuild().getIdLong()
      DiscordLinkable link = runtime.getLoader().getDiscordLinks().ensureByUser(member.getUser());
      Optional<UserData> optional =
          link.getLinkedUserId()
              .flatMap(linkedUserId -> runtime.getLoader().getUsers().getById(linkedUserId));
      if (optional.isPresent()) {
        playing = handler.getPlaying(optional.get());
        single = link.getPublicDisplayName(runtime.getLoader());
      } else {
        playing = handler.getPlaying(sender);
      }
    } else {
      playing = handler.getPlaying(sender);
    }
    for (MinecraftMatch match : playing) {
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
          MinecraftLinkable[] linkables) {
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("win.cannot-global"));
    if (linkables.length == 0) return Result.of(locale.get("win.mention-one"));
    MatchEloCalculator eloHandler = Guido.getHandlers().getHandler(MatchEloCalculator.class);
    RanksHandler ranksHandler = Guido.getHandlers().getHandler(RanksHandler.class);
    for (MinecraftLinkable linkable : linkables) {
      eloHandler.setElo(linkable, true, ladder, true);
      ranksHandler.update(linkable);
    }
    return Result.of(locale.get("win.updated"));
  }

  @GuidoJdaPermission("guido.lose")
  @Command(aliases = "lose", description = "lose.desc")
  public Result lose(
      LocaleFile locale,
      GuidoGuild guild,
      @Required(name = "lose.ladder", description = "lose.ladder.desc") Ladder ladder,
      @Required(name = "lose.entities", description = "lose.entities.desc")
          MinecraftLinkable[] linkables) {
    if (ladder instanceof GlobalLadder) return Result.of(locale.get("lose.cannot-global"));
    if (linkables.length == 0) return Result.of(locale.get("lose.mention-one"));
    MatchEloCalculator handler = Guido.getHandlers().getHandler(MatchEloCalculator.class);
    RanksHandler ranksHandler = Guido.getHandlers().getHandler(RanksHandler.class);
    for (MinecraftLinkable linkable : linkables) {
      handler.setElo(linkable, false, ladder, true);
      ranksHandler.update(linkable);
    }
    return Result.of(locale.get("lose.updated"));
  }
}
