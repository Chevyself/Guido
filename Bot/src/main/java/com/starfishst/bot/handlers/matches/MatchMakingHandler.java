package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.api.events.queue.QueueJoinEvent;
import com.starfishst.bot.api.events.queue.QueueLeaveEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.links.LinkedData;
import com.starfishst.guido.api.data.matches.Ladder;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.matches.Queue;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/** This handles the match-making process for ranked matches */
public class MatchMakingHandler implements GuidoEventHandler {

  /** The queues that are working right now in the handler */
  @NotNull private final Set<Queue> queues = new HashSet<>();

  /** The waiting channel for the users in queue */
  @NotNull private final Map<Long, Long> waitingChannels = new HashMap<>();

  @NotNull private final Map<String, MatchHandler> handlers = new HashMap<>();

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
    if (guildId != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildData(guildId);
      TextChannel channel = guildData.getChannel("matches");
      EmbedQuery information = match.getInformation(locale);
      information
          .getEmbedBuilder()
          .setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
      information.send(channel);
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueJoin(QueueJoinEvent event) {
    Match match = event.getQueue().checkReady();
  }

  @SubscribeEvent
  public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
    VoiceChannel channel = event.getChannelLeft();
    long guildId = event.getGuild().getIdLong();
    long channelId = this.waitingChannels.getOrDefault(guildId, -1L);
    if (channelId == channel.getIdLong()) {
      BotLinkedData member =
          Guido.getDataLoader().getMemberData(event.getMember().getIdLong(), guildId);
      UserData user = member.getLinkedUser();
      if (user != null) {
        Collection<LinkedData> links = Guido.getDataLoader().getLinks(user);
        for (LinkedData link : links) {
          for (Queue queue : this.getQueues(guildId)) {
            queue.leave(link.getInfo());
          }
        }
      }
      if (channel.getMembers().size() < 1) {
        this.waitingChannels.remove(guildId);
        channel.delete().queue();
      }
    }
  }

  @SubscribeEvent
  public void onQueueLeaveEvent(QueueLeaveEvent event) {}

  /**
   * Get the waiting channel for a guild
   *
   * @param guild the guild waiting for a channel
   * @return the channel
   */
  @NotNull
  private VoiceChannel getWaitingChannel(BotGuild guild) {
    long id =
        this.waitingChannels.computeIfAbsent(
            guild.getId(),
            aLong ->
                guild.getCategory("matches").createVoiceChannel("Queue").complete().getIdLong());
    VoiceChannel channel = guild.getDiscord().getVoiceChannelById(id);
    if (channel == null) {
      this.waitingChannels.remove(guild.getId());
      return this.getWaitingChannel(guild);
    } else {
      return channel;
    }
  }

  private Set<Queue> getQueues(long guildId) {
    Set<Queue> queues = new HashSet<>();
    for (Queue queue : this.queues) {
      if (queue.getGuildId() == guildId) {
        queues.add(queue);
      }
    }
    return queues;
  }

  @NotNull
  private Queue getQueue(@NotNull BotGuild guild, @NotNull Ladder ladder) {
    Set<Queue> queues = this.getQueues(guild.getId());
    for (Queue queue : queues) {
      if (queue.getLadderName().equalsIgnoreCase(ladder.getName())) {
        return queue;
      }
    }
    Queue queue = ladder.createQueue(guild);
    queues.add(queue);
    return queue;
  }

  public boolean joinQueue(
      @NotNull BotGuild guild, @NotNull Member member, @NotNull Ladder ladder) {
    if (this.getQueue(guild, ladder)
        .join(Guido.getDataLoader().getMemberData(member.getIdLong(), guild.getId()).getInfo())) {
      guild.getDiscord().moveVoiceMember(member, this.getWaitingChannel(guild)).queue();
      return true;
    }
    return false;
  }

  public Collection<Queue> getQueues(BotLinkedInfo info) {
    Set<Queue> queues = new HashSet<>();
    for (Queue queue : this.queues) {
      if (queue.isWaiting(info)) {
        queues.add(queue);
      }
    }
    return queues;
  }

  @Override
  public void close() {}
}
