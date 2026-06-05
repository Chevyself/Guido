package me.googas.bot.core.handlers.ranks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.links.*;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.MatchTeamMember;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Stateables;
import me.googas.bot.BotJdaProvider;
import me.googas.bot.DiscordRankRange;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.events.data.links.LinkableRankUpdatedEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import me.googas.starbox.logging.LoggerFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/** Handles decorations for linked data */
public class RanksHandler implements GuidoHandler {

  @NonNull private static final Logger logger = LoggerFactory.getLogger(RanksHandler.class);

  @NonNull private final GuidoBotRuntime runtime;

  public RanksHandler(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  /**
   * Listen to when a match ends to update ranks
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NonNull MinecraftMatchStatusUpdatedEvent event) {
    this.update(event.getMatch(), false);
  }

  /**
   * Update the ranks from a abstractMatch
   *
   * @param match the abstractMatch to update the ranks
   * @param event whether to call the event of ranks updated
   */
  public void update(@NonNull Match match, boolean event) {
    for (MatchTeam matchTeam : match.getTeams()) {
      for (MatchTeamMember teamMember : matchTeam.getMembers()) {
        Optional<? extends Linkable> optional =
            teamMember.getLinkable(runtime.getLinkableMatcher());
        if (optional.isEmpty()) {
          logger.warning(
              String.format(
                  "Failed to update team member %s, could not find linkable", teamMember));
          return;
        }
        Linkable data = optional.get();
        UpdateResult update = this.update(data);
        if (event) runtime.getListeners().call(new LinkableRankUpdatedEvent(data, update));
      }
    }
  }

  @NonNull
  public UpdateResult update(@NonNull Linkable linkable) {
    UpdateResult result = new UpdateResult();
    Collection<Ladder> ladders = runtime.getLadderProvider().getLadders();
    Collection<DiscordRankRange> ranges = runtime.getRanksProvider().getRanks();
    for (Ladder ladder : ladders) {
      double elo = linkable.getStats(runtime.getStatsProvider()).getElo(ladder, ladders);
      result.append(this.update(elo, ranges));
    }
    Optional<DiscordLinkable> optional = runtime.getLinkableMatcher().getDiscord(linkable);
    if (optional.isEmpty()) {
      logger.warning(
          String.format(
              "Failed to update discord for linkable %s, could not find Discord", linkable));
      return result;
    }
    DiscordLinkable discord = optional.get();
    this.updateDiscord(linkable, result, discord, ladders);
    return result;
  }

  public void updateDiscord(
      @NonNull Linkable linkable,
      UpdateResult result,
      @NonNull DiscordLinkable discord,
      Collection<Ladder> ladders) {
    BotJdaProvider jdaProvider = runtime.getBotJda();
    Optional<Member> optional = discord.getMember(jdaProvider);
    if (optional.isEmpty()) {
      logger.warning(
          String.format(
              "Failed to update discord for linkable %s, could not find Guild member", linkable));
      return;
    }
    Member member = optional.get();
    Guild guild = jdaProvider.getGuild();
    result
        .getApplied()
        .removeIf(
            range -> {
              Role role = guild.getRoleById(range.getRoleId());
              if (role != null) {
                if (!member.getRoles().contains(role)) {
                  guild.addRoleToMember(member, role).queue();
                  return false;
                }
              }
              return true;
            });
    result
        .getRemoved()
        .removeIf(
            range -> {
              Role role = guild.getRoleById(range.getRoleId());
              if (role != null) {
                if (member.getRoles().contains(role)) {
                  guild.removeRoleFromMember(member, role).queue();
                  return false;
                }
              }
              return true;
            });
    this.updateNickname(linkable, member, ladders);
  }

  public void updateNickname(
      @NonNull Linkable linkable, Member member, Collection<Ladder> ladders) {
    if (!member.isOwner()) {
      String nick = linkable.getPublicDisplayName(runtime.getLinkableMatcher());
      member
          .modifyNickname(
              "["
                  + (int)
                      linkable
                          .getStats(runtime.getStatsProvider())
                          .getElo(GlobalLadder.INSTANCE, ladders)
                  + "] - "
                  + nick)
          .queue();
    }
  }

  public UpdateResult update(double elo, @NonNull Collection<DiscordRankRange> ranges) {
    return new UpdateResult(
        Stateables.getApplying(elo, ranges), Stateables.getOutside(elo, ranges));
  }

  @Override
  public void onDisable() {}

  /** This is a result of the ranks that have been applied or removed */
  public static class UpdateResult {
    @NonNull @Getter private final List<DiscordRankRange> applied;
    @NonNull @Getter private final List<DiscordRankRange> removed;

    public UpdateResult() {
      this(new ArrayList<>(), new ArrayList<>());
    }

    public UpdateResult(
        @NonNull List<DiscordRankRange> applied, @NonNull List<DiscordRankRange> removed) {
      this.applied = applied;
      this.removed = removed;
    }

    @NonNull
    public UpdateResult append(@NonNull UpdateResult result) {
      this.applied.addAll(result.getApplied());
      this.removed.addAll(result.getRemoved());
      return this;
    }
  }
}
