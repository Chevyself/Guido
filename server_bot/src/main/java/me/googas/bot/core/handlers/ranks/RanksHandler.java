package me.googas.bot.core.handlers.ranks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.links.*;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.stats.Stats;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.Stateables;
import me.googas.bot.GuidoJdaProvider;
import me.googas.bot.api.events.data.links.LinkableRankUpdatedEvent;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.server.RankRange;
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
  public void update(@NonNull MinecraftMatch match, boolean event) {
    for (MinecraftMatchTeam matchTeam : match.getTeams()) {
      for (MinecraftMatchTeamMember teamMember : matchTeam.getMembers()) {
        Optional<? extends MinecraftLinkable> optional =
            teamMember.getLinkable(runtime.getLoader());
        if (optional.isEmpty()) {
          logger.warning(
              String.format(
                  "Failed to update team member %s, could not find linkable", teamMember));
          return;
        }
        MinecraftLinkable data = optional.get();
        UpdateResult update = this.update(data);
        if (event) runtime.getListeners().call(new LinkableRankUpdatedEvent(data, update));
      }
    }
  }

  @NonNull
  public UpdateResult update(@NonNull MinecraftLinkable linkable) {
    UpdateResult result = new UpdateResult();
    ImmutableCollection<? extends Ladder> ladders = runtime.getLadderProvider().getLadders();
    ImmutableCollection<? extends RankRange> ranges = runtime.getRanksProvider().getRanks();
    for (Ladder ladder : ladders) {
      double elo =
          runtime
              .getLoader()
              .getStats()
              .getForMinecraftLink(linkable, Stats.EMPTY_CONTEXT)
              .getElo(ladder);
      result.append(this.update(elo, ranges));
    }
    Optional<DiscordLinkable> optional =
        linkable
            .getLinkedUserId()
            .flatMap(
                linkedUserId ->
                    runtime.getLoader().getDiscordLinks().getByLinkedUser(linkedUserId));
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
      @NonNull MinecraftLinkable linkable,
      UpdateResult result,
      @NonNull DiscordLinkable discord,
      ImmutableCollection<? extends Ladder> ladders) {
    GuidoJdaProvider jdaProvider = runtime.getBotJda();
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
      @NonNull MinecraftLinkable linkable,
      Member member,
      ImmutableCollection<? extends Ladder> ladders) {
    if (!member.isOwner()) {
      String nick = linkable.getPublicDisplayName(runtime.getLoader());
      member
          .modifyNickname(
              "["
                  + (int)
                      runtime
                          .getLoader()
                          .getStats()
                          .getForMinecraftLink(linkable, Stats.EMPTY_CONTEXT)
                          .getElo(GlobalLadder.INSTANCE, ladders)
                  + "] - "
                  + nick)
          .queue();
    }
  }

  public UpdateResult update(double elo, @NonNull ImmutableCollection<? extends RankRange> ranges) {
    return new UpdateResult(
        Stateables.getApplying(elo, ranges), Stateables.getOutside(elo, ranges));
  }

  @Override
  public void onDisable() {}

  /** This is a result of the ranks that have been applied or removed */
  public static class UpdateResult {
    @NonNull @Getter private final List<RankRange> applied;
    @NonNull @Getter private final List<RankRange> removed;

    public UpdateResult() {
      this(new ArrayList<>(), new ArrayList<>());
    }

    public UpdateResult(@NonNull List<RankRange> applied, @NonNull List<RankRange> removed) {
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
