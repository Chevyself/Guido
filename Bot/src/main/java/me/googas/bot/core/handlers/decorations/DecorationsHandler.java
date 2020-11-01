package me.googas.bot.core.handlers.decorations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedDataType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkedData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/** Handles decorations for linked data */
public class DecorationsHandler implements GuidoEventHandler {

  /**
   * Listen to when a match ends to update decorations
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NotNull MatchStatusUpdatedEvent event) {
    Match match = event.getMatch();
    long guildId = match.getGuildId();
    String ladderName = match.getDetails().getValue("ladder", String.class);
    if (ladderName != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        for (Team team : event.getMatch().getTeams()) {
          for (TeamMember teamMember : team.getMembers()) {
            LinkedData data = teamMember.getLinkInfo().getLink();
            if (data instanceof BotLinkedData) {
              Member member = ((BotLinkedData) data).getDiscordMember(guildId);
              if (member != null) {
                float elo = data.getElo(ladder);
                float global = data.getGlobalElo(guildData);
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
                  if (data.getType() == LinkedDataType.MINECRAFT) {
                    nick = data.getSingle();
                  } else {
                    Collection<LinkedData> links = data.getLinks(LinkedDataType.MINECRAFT);
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
