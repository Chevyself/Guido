package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.api.events.queue.QueueJoinEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.bot.util.console.Console;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Match;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** This handles the match-making process for ranked matches */
public class MatchMakingHandler implements GuidoEventHandler {

  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NotNull MatchStatusUpdatedEvent event) {
    BotMatch match = event.getMatch();
    LocaleFile locale = Guido.getLanguageHandler().getDefault();
    Long guildId = match.getDetails().getValue("guild", Long.class);
    Console.debug(match + " has updated its event to " + event.getStatus());
    if (guildId != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      TextChannel channel = guildData.getChannel("matches");
      EmbedQuery information = match.getInformation(locale);
      information
          .getEmbedBuilder()
          .setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
      information.send(channel);
      Console.debug(match + " has been announced in " + channel);
    }
  }

  /**
   * Check if a match is ready when an link joins the queue
   *
   * @param event the event of a link joining a queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueJoin(QueueJoinEvent event) {
    Match match = event.getQueue().checkReady();
    Console.debug("Is the queue " + event.getQueue() + " ready to create a match? " + match);
    if (match != null) {
      for (LinkedInfo participant : match.getParticipants()) {
        Guido.getHandler(QueueHandler.class).leaveQueue(participant);
      }
    }
  }

  @Override
  public void close() {}
}
