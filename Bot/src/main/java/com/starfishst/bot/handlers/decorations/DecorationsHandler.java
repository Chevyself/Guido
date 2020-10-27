package com.starfishst.bot.handlers.decorations;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamMember;
import java.util.ArrayList;
import java.util.List;
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
    BotMatch match = event.getMatch();
    Long guildId = match.getDetails().getValue("guild", Long.class);
    String ladderName = match.getDetails().getValue("ladder", String.class);
    if (guildId != null && ladderName != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        for (Team team : event.getMatch().getTeams()) {
          for (TeamMember teamMember : team.getMembers()) {
            LinkedData data = teamMember.getLinkInfo().getLink();
            if (data != null && data.getType() == LinkedDataType.DISCORD_GUILD) {
              double elo = data.getElo(ladder);
              double global = data.getGlobalElo(guildData);
              List<Role> toAdd =
                  new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, true));
              List<Role> toRemove =
                  new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, false));
              toAdd.addAll(guildData.getGlobalRolesDiscord((int) global, true));
              toRemove.addAll(guildData.getGlobalRolesDiscord((int) global, false));
              if (data instanceof BotLinkedData) {
                Member member = ((BotLinkedData) data).getDiscordMember();
                if (member != null) {
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
                }
              }
              /*
              Save this for latter to add the name of the users
              if (!member.isOwner()) {
                member.modifyNickname(String.valueOf((int) global)).queue(ignoredVoid -> {}, ignoredException -> {
                  // Exception can be thrown if user cannot be edited
                });
              }
               */
            }
          }
        }
      }
    }
  }

  @Override
  public void close() {}
}
