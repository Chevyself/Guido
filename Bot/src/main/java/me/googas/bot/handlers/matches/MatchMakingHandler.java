package me.googas.bot.handlers.matches;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashSet;
import me.googas.api.UserData;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.bot.Guido;
import me.googas.bot.api.data.BotGuild;
import me.googas.bot.api.data.BotMatch;
import me.googas.bot.api.data.loader.BotDataLoader;
import me.googas.bot.api.events.match.MatchLoadedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.events.queue.QueueJoinEvent;
import me.googas.bot.handlers.GuidoEventHandler;
import me.googas.bot.util.console.Console;
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
    Match match = event.getMatch();
    LocaleFile locale = Guido.getLanguageHandler().getDefault();
    Long guildId = match.getDetails().getValue("guild", Long.class);
    Console.debug(match + " has updated its event to " + event.getStatus());
    if (guildId != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      if (match instanceof BotMatch) {
        TextChannel channel = guildData.getChannel("matches");

        EmbedQuery information = ((BotMatch) match).getInformation(locale);
        information
            .getEmbedBuilder()
            .setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
        information.send(channel);
        Console.debug(match + " has been announced in " + channel);
      }
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
      match.addToCache();
      new MatchLoadedEvent(match).call();
      for (LinkedInfo participant : match.getParticipants()) {
        Guido.getHandler(QueueHandler.class).leaveQueue(participant);
      }
    }
  }

  /**
   * Get whether an user is playing
   *
   * @param data the user to check if playing
   * @return true if the user is playing
   */
  public boolean isPlaying(@NotNull UserData data) {
    return !this.getPlaying(data).isEmpty();
  }

  /**
   * Get whether an user is playing
   *
   * @param info the user to check if playing
   * @return true if the user is playing
   */
  public boolean isPlaying(@NotNull LinkedInfo info) {
    LinkedData link = info.getLink();
    if (link != null) {
      UserData user = link.getLinkedUser();
      if (user != null) {
        return this.getPlaying(user).isEmpty();
      } else {
        return this.getPlaying(info).isEmpty();
      }
    }
    return false;
  }

  /**
   * Get where an user is playing
   *
   * @param data the user to check where it is playing
   * @return the collection of matches where the user is playing
   */
  public Collection<Match> getPlaying(@NotNull UserData data) {
    BotDataLoader loader = Guido.getDataLoader();
    Collection<LinkedData> links = loader.getLinks(data);
    Collection<Match> participating = new HashSet<>();
    for (LinkedData link : links) {
      participating.addAll(
          loader.getParticipating(
              link.getType(),
              link.getIdentification(),
              MatchStatus.PLAYING,
              MatchStatus.READY,
              MatchStatus.STARTING,
              MatchStatus.WAITING));
    }
    return participating;
  }

  /**
   * Get where a link is playing
   *
   * @param info the link to check where it is playing
   * @return the collection of matches where the user is playing
   */
  public Collection<Match> getPlaying(@NotNull LinkedInfo info) {
    BotDataLoader loader = Guido.getDataLoader();
    return loader.getParticipating(
        info.getType(),
        info.getIdentification(),
        MatchStatus.PLAYING,
        MatchStatus.READY,
        MatchStatus.STARTING,
        MatchStatus.WAITING);
  }

  @Override
  public void close() {}
}
