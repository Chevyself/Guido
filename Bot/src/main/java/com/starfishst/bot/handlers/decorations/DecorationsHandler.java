package com.starfishst.bot.handlers.decorations;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.data.BotTeam;
import com.starfishst.bot.api.events.data.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Ladder;
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
      BotGuild guildData = Guido.getDataLoader().getGuildData(guildId);
      Ladder ladder = guildData.getLadder(ladderName);
      if (ladder != null) {
        for (BotTeam team : event.getMatch().getTeams()) {
          for (BotLinkedInfo info : team.getMembers().keySet()) {
            BotLinkedData data = info.getData();
            if (data != null && data.getType() == LinkedDataType.DISCORD_GUILD) {
              Member member = data.getDiscordMember();
              if (member != null) {
                double elo = data.getElo(ladder);
                double global = data.getGlobalElo(guildData);
                List<Role> toAdd =
                    new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, false));
                List<Role> toRemove =
                    new ArrayList<>(guildData.getRolesDiscord(ladder, (int) elo, true));
                toAdd.addAll(guildData.getGlobalRolesDiscord((int) global, false));
                toRemove.addAll(guildData.getGlobalRolesDiscord((int) global, true));
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
  }

  @Override
  public void close() {}
}
