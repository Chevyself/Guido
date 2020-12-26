package me.googas.bot.core.handlers.decorations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.bot.Guido;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.links.BotLinkable;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/** Handles decorations for linked data */
public class DecorationsHandler implements GuidoEventHandler {

  /**
   * Listen to when a match ends to update decorations
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
            if (data instanceof BotLinkable) {
              Member member = ((BotLinkable) data).getDiscordMember(guildId);
              if (member != null) {
                float elo = data.getElo(ladder);
                float global = data.getGlobalElo(guildData.getLadders());
                List<Role> toAdd =
                    new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, true));
                List<Role> toRemove =
                    new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, false));
                toAdd.addAll(guildData.getGlobalRolesDiscord((int) global, true));
                toRemove.addAll(guildData.getGlobalRolesDiscord((int) global, false));
                for (Role role : toAdd) {
                  if (!member.getRoles().contains(role)) {
                    member.getGuild().addRoleToMember(member, role).queue();
                  }
                }
                for (Role role : toRemove) {
                  if (member.getRoles().contains(role)) {
                    member.getGuild().removeRoleFromMember(member, role).queue();
                  }
                }
                if (!member.isOwner()) {
                  String nick =
                      member.getEffectiveName().contains(" - ")
                          ? member.getEffectiveName().split(" - ")[1]
                          : member.getEffectiveName();
                  if (data.getType() == LinkableType.MINECRAFT) {
                    nick = data.getSingle();
                  } else {
                    Collection<Linkable> links = data.getLinks(LinkableType.MINECRAFT);
                    if (!links.isEmpty()) {
                      nick = links.iterator().next().getSingle();
                    }
                  }
                  member.modifyNickname("[" + (int) global + "] - " + nick).queue();
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public void close() {}
}
