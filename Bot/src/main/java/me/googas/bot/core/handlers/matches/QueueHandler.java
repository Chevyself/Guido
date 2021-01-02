package me.googas.bot.core.handlers.matches;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.matches.queue.Queueable;
import me.googas.bot.Guido;
import me.googas.bot.api.events.queue.QueueLeaveEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Handles the queue */
public class QueueHandler implements GuidoHandler {

  /** The queues that are working right now in the handler */
  @NonNull private final Set<Queue> queues = new HashSet<>();

  /** The waiting channel for the users in queue */
  @NonNull private final Map<Long, Long> waitingChannels = new HashMap<>();

  /**
   * Check when a member leaves a voice channel. If the voice channel is the queues waiting channel
   * then remove them from the queue
   *
   * @param event the event of a member leaving a voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceLeave(@NonNull GuildVoiceLeaveEvent event) {
    this.checkRemoveQueue(event.getChannelLeft(), event.getGuild(), event.getMember());
  }

  /**
   * Check when a member leaves a voice channel. If the voice channel is the queues waiting channel
   * then remove them from the queue
   *
   * @param event the event of a member leaving a voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceMove(@NonNull GuildVoiceMoveEvent event) {
    this.checkRemoveQueue(event.getChannelLeft(), event.getGuild(), event.getMember());
    this.joinQueueFromVoice(
        event.getGuild().getIdLong(), event.getChannelJoined(), event.getMember());
  }

  /**
   * Make a player join a queue from voice channel
   *
   * @param guildId the id of the guild where this is happening
   * @param channelJoined the channel that the member joined
   * @param member the member that joined the channel
   */
  public void joinQueueFromVoice(
      long guildId, @NonNull VoiceChannel channelJoined, @NonNull Member member) {
    BotGuild guild = Guido.getDataLoader().getGuildDataOrCreate(guildId);
    String key = guild.getVoiceChannel(channelJoined.getIdLong());
    if (key != null && key.startsWith("join-")) {
      String ladderName = key.substring(5);
      Ladder ladder = guild.getLadder(ladderName);
      if (ladder == null) return;
      this.joinQueue(guild, member, ladder);
    }
  }

  @SubscribeEvent
  public void onGuildVoiceJoinEvent(@NonNull GuildVoiceJoinEvent event) {
    this.joinQueueFromVoice(
        event.getGuild().getIdLong(), event.getChannelJoined(), event.getMember());
  }

  /**
   * Check if the user that left the channel may leave the queue
   *
   * @param channelLeft the channel that the user left
   * @param guild the guild in which the event happened
   * @param disc the event that left the queue
   */
  public void checkRemoveQueue(
      @NonNull VoiceChannel channelLeft, @NonNull Guild guild, @NonNull Member disc) {
    long guildId = guild.getIdLong();
    long channelId = this.waitingChannels.getOrDefault(guildId, -1L);
    if (channelId == channelLeft.getIdLong()) {
      Linkable member =
          Guido.getDataLoader()
              .getLink(LinkableType.DISCORD, new GuidoValuesMap("id", disc.getIdLong()));
      for (Linkable data : member.getLinks()) {
        this.leaveQueue(data.getInfo());
      }
      this.checkDeletion(channelLeft, guildId);
    }
  }

  /**
   * When an user leaves the queue make then leave the voice channel
   *
   * @param event the event of an user leaving the queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueLeave(QueueLeaveEvent event) {
    Queueable data = event.getData();
    if (!(data instanceof LinkableInfo)) return;
    Linkable link = ((LinkableInfo) data).getLink();
    if (link != null) {
      Member member =
          link.requireDiscordRef()
              .getMember(event.getQueue().getGuildId(), Guido.getConnection().validatedJda());
      if (member != null) {
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState != null) {
          VoiceChannel channel = voiceState.getChannel();
          if (channel != null
              && channel.getIdLong()
                  == this.waitingChannels.getOrDefault(event.getQueue().getGuildId(), -1L)) {
            member.getGuild().moveVoiceMember(member, null).queue();
          }
        }
      }
    }
  }

  /**
   * Makes the given info leave all the queues where it is waiting
   *
   * @param info the information of the data to leave all queues
   */
  public void leaveQueue(@NonNull LinkableInfo info) {
    for (Queue queue : this.getQueues(info)) {
      queue.leave(info);
    }
  }

  /**
   * Check whether the waiting channel should be deleted
   *
   * @param channel the channel which is being checked whether to delete it
   * @param guildId the id of the guild
   */
  private void checkDeletion(@NonNull VoiceChannel channel, long guildId) {
    List<Member> members = channel.getMembers();
    if (members.isEmpty()) {
      this.waitingChannels.remove(guildId);
      channel.delete().queue();
    }
  }

  /**
   * Get the waiting channel for a guild
   *
   * @param guild the guild waiting for a channel
   * @return the channel
   */
  @NonNull
  private VoiceChannel getWaitingChannel(BotGuild guild) {
    long id =
        this.waitingChannels.computeIfAbsent(
            guild.getId(),
            aLong ->
                guild.getCategory("matches").createVoiceChannel("Queue").complete().getIdLong());
    VoiceChannel channel = guild.toDiscord().getVoiceChannelById(id);
    if (channel == null) {
      this.waitingChannels.remove(guild.getId());
      return this.getWaitingChannel(guild);
    } else {
      return channel;
    }
  }

  /**
   * Get all the queues for a guild
   *
   * @param guildId the guild querying for queues
   * @return the queues for the given guild
   */
  private Set<Queue> getQueues(long guildId) {
    Set<Queue> queues = new HashSet<>();
    for (Queue queue : this.queues) {
      if (queue.getGuildId() == guildId) {
        queues.add(queue);
      }
    }
    return queues;
  }

  /**
   * Get the queue for certain ladder in a guild
   *
   * @param guild the guild to get the queue from
   * @param ladder the ladder that needs the queue
   * @return the queue if exists else a new one will be created from {@link
   *     Ladder#createQueue(long)})}
   */
  @NonNull
  public Queue getQueue(@NonNull BotGuild guild, @NonNull Ladder ladder) {
    Set<Queue> queues = this.getQueues(guild.getId());
    for (Queue queue : queues) {
      if (queue.getLadderName().equalsIgnoreCase(ladder.getName())) {
        return queue;
      }
    }
    Queue queue = ladder.createQueue(guild.getId());
    this.queues.add(queue);
    return queue;
  }

  /**
   * Makes a member join a queue
   *
   * @param guild the guild where the queue is happening
   * @param member the member entering the queue
   * @param ladder the ladder to get the queue of it
   * @return whether the member joined the queue
   */
  public QueueResult joinQueue(
      @NonNull BotGuild guild, @NonNull Member member, @NonNull Ladder ladder) {
    Queue queue = this.getQueue(guild, ladder);
    QueueResult join =
        queue.join(
            Guido.getDataLoader()
                .getLink(LinkableType.DISCORD, new GuidoValuesMap("id", member.getIdLong()))
                .getInfo());
    if (join.isCancelled()) return join;
    guild.toDiscord().moveVoiceMember(member, this.getWaitingChannel(guild)).queue();
    return new QueueResult();
  }

  /**
   * Get all the queues where the waiting info is waiting
   *
   * @param info the information of a link
   * @return the collection of queues where the link is waiting
   */
  public Collection<Queue> getQueues(@NonNull LinkableInfo info) {
    Set<Queue> queues = new HashSet<>();
    for (Queue queue : this.queues) {
      if (queue.isWaiting(info)) {
        queues.add(queue);
      }
    }
    return queues;
  }

  /**
   * Get whether a member is in queue
   *
   * @param guild the guild to
   * @param member the member to check
   * @param ladder the ladder to get the queue
   * @return true if the member is waiting in the queue
   */
  public boolean isWaiting(
      @NonNull BotGuild guild, @NonNull Member member, @NonNull Ladder ladder) {
    Linkable memberData =
        Guido.getDataLoader()
            .getLink(LinkableType.DISCORD, new GuidoValuesMap("id", member.getIdLong()));
    Queue queue = this.getQueue(guild, ladder);
    for (Linkable link : memberData.getLinks()) {
      if (queue.isWaiting(link.getInfo())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void close() {}
}
