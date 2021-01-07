package me.googas.bot.core.handlers.queue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.queue.Queue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.bot.api.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Handles the queue */
public class QueueHandler implements GuidoHandler {

  /** The queues that are working right now in the handler */
  @NonNull private final Set<Queue> queues = new HashSet<>();

  /**
   * Make a player join a queue from voice channel
   *
   * @param guildId the id of the guild where this is happening
   * @param channelJoined the channel that the member joined
   * @param member the member that joined the channel
   */
  public void joinQueueFromVoice(
      long guildId, @NonNull VoiceChannel channelJoined, @NonNull Member member) {
    BotGuild guild = Guido.getDiscordLoader().getGuild(guildId);
    String key = guild.getVoiceChannel(channelJoined.getIdLong());
    if (key != null && key.startsWith("join-")) {
      String ladderName = key.substring(5);
      Ladder ladder = guild.getLadder(ladderName);
      if (ladder == null) return;
      QueueResult result = this.joinQueue(guild, member, ladder);
      if (result.isCancelled())
        member
            .getUser()
            .openPrivateChannel()
            .queue(
                channel -> {
                  channel.sendMessage(result.getReason()).queue();
                });
    }
  }

  @SubscribeEvent
  public void onGuildVoiceJoinEvent(@NonNull GuildVoiceJoinEvent event) {
    this.joinQueueFromVoice(
        event.getGuild().getIdLong(), event.getChannelJoined(), event.getMember());
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
    QueueResult join = queue.join(Discord.getUser(member).getInfo());
    if (join.isCancelled()) return join;
    guild.toDiscord().moveVoiceMember(member, this.channels().getWaitingChannel(guild)).queue();
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
    DiscordLinkable memberData = Discord.getUser(member);
    Queue queue = this.getQueue(guild, ladder);
    for (Linkable link : memberData.getLinks()) {
      if (queue.isWaiting(link.getInfo())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the queue channels handler
   *
   * @return the queue channels handler
   */
  @NonNull
  private QueueChannelsHandler channels() {
    return Guido.getHandlers().getHandler(QueueChannelsHandler.class);
  }

  @Override
  public void onDisable() {}
}
