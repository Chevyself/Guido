package me.googas.bot.core.handlers.ranks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Stateables;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.ranks.RankRange;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.api.events.data.links.LinkableRankUpdatedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.bot.core.util.Stats;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/** Handles decorations for linked data */
public class RanksHandler implements GuidoEventHandler {

  /**
   * Get the ladders to apply an update of decorations
   *
   * @return the ladders to apply the update
   */
  public static List<Ladder> getLadders(@Nullable String ladderName, @NonNull BotGuild guild) {
    List<Ladder> ladders = new ArrayList<>();
    Ladder ladder = ladderName == null ? null : guild.getLadder(ladderName);
    if (ladder == null) {
      ladders.addAll(guild.getLadders());
    } else {
      ladders.add(ladder);
    }
    ladders.add(GlobalLadder.INSTANCE);
    return ladders;
  }

  /**
   * Listen to when a match ends to update ranks
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NonNull MatchStatusUpdatedEvent event) {
    Match match = event.getMatch();
    long guildId = match.getGuildId();
    String ladderName = match.getDetails().get("ladder", String.class);
    if (ladderName != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        for (MatchTeam matchTeam : event.getMatch().getTeams()) {
          for (TeamMember teamMember : matchTeam.getMembers()) {
            Linkable data = teamMember.getLinkInfo().getLink();
            if (data == null) return;
            new LinkableRankUpdatedEvent(data, this.update(data, guildData)).call();
          }
        }
      }
    }
  }

  @NonNull
  public UpdateResult update(@NonNull Linkable linkable, @NonNull BotGuild guild) {
    return this.update(linkable, guild.getLadders(), guild.getRanges(), guild);
  }

  @NonNull
  public UpdateResult update(
      @NonNull Linkable linkable,
      @NonNull Collection<Ladder> ladders,
      @NonNull Collection<RankRange> ranges,
      @Nullable BotGuild guild) {
    UpdateResult result = new UpdateResult();
    DiscordLinkable discord = linkable.toDiscordRef();
    for (Ladder ladder : ladders) {
      float elo = Stats.getElo(linkable, ladder, ladders);
      result.append(this.update(elo, ranges));
    }
    this.updateDiscord(linkable, guild, result, discord);
    return result;
  }

  /**
   * This will get which roles the user didn't or did have to update the result TODO a better
   * javadoc
   *
   * @param guild
   * @param result
   * @param discord
   */
  public void updateDiscord(
      @NonNull Linkable linkable,
      @Nullable BotGuild guild,
      UpdateResult result,
      DiscordLinkable discord) {
    if (guild != null && discord != null) {
      Guild guildDiscord = guild.getDiscord();
      Member member = discord.getMember(guildDiscord);
      result
          .getApplied()
          .removeIf(
              range -> {
                Role role =
                    guildDiscord.getRoleById(range.getPreferences().getOr("id", Long.class, 0L));
                if (role != null) {
                  if (!member.getRoles().contains(role)) {
                    guildDiscord.addRoleToMember(member, role).queue();
                    return false;
                  }
                }
                return true;
              });
      result
          .getRemoved()
          .removeIf(
              range -> {
                Role role =
                    guildDiscord.getRoleById(range.getPreferences().getOr("id", Long.class, 0L));
                if (role != null) {
                  if (member.getRoles().contains(role)) {
                    guildDiscord.removeRoleFromMember(member, role).queue();
                    return false;
                  }
                }
                return true;
              });
      this.updateNickname(linkable, guild, member);
    }
  }

  public void updateNickname(@NonNull Linkable linkable, @NonNull BotGuild guild, Member member) {
    if (!member.isOwner()) {
      String nick =
          member.getEffectiveName().contains(" - ")
              ? member.getEffectiveName().split(" - ")[1]
              : member.getEffectiveName();
      if (linkable.getType() == LinkableType.MINECRAFT) {
        nick = linkable.getSingle();
      } else {
        UserData user = linkable.getLinkedUser();
        if (user != null) {
          Linkable link = user.getLink(LinkableType.MINECRAFT);
          if (link != null) nick = link.getSingle();
        }
      }
      member
          .modifyNickname(
              "["
                  + (int) Stats.getElo(linkable, GlobalLadder.INSTANCE, guild.getLadders())
                  + "] - "
                  + nick)
          .queue();
    }
  }

  public UpdateResult update(float elo, @NonNull Collection<RankRange> ranges) {
    return new UpdateResult(
        Stateables.getApplying(elo, ranges), Stateables.getOutside(elo, ranges));
  }

  @Override
  public void close() {}

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
