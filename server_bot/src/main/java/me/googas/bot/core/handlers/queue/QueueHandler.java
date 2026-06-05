package me.googas.bot.core.handlers.queue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Handles the queue */
public class QueueHandler implements GuidoHandler {

  @NonNull @Getter private final GuidoBotRuntime runtime;

  /** The queues that are working right now in the handler */
  @NonNull @Getter private final Set<MinecraftQueue> queues = new HashSet<>();

  public QueueHandler(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  /**
   * Make a player join a queue from voice channel
   *
   * @param guildId the id of the guild where this is happening
   * @param channelJoined the channel that the member joined
   * @param member the member that joined the channel
   */
  public void joinQueueFromVoice(
      long guildId, AudioChannelUnion channelJoined, @NonNull Member member) {
    if (channelJoined == null) return;
    GuidoGuild guild = Guido.getHandlers().getDiscordLoader().getGuild(guildId);
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
  public void onGuildVoiceJoinEvent(@NonNull GuildVoiceUpdateEvent event) {
    this.joinQueueFromVoice(
        event.getGuild().getIdLong(), event.getChannelJoined(), event.getMember());
  }

  /**
   * Makes the given info leave all the queues where it is waiting
   *
   * @param info the information of the data to leave all queues
   */
  public void leaveQueue(@NonNull MinecraftLinkable info) {
    for (MinecraftQueue queue : this.getQueues(info)) {
      queue.leave(info);
    }
  }

  public void leaveQueue(@NonNull MinecraftMatchTeamMember member) {
    for (MinecraftQueue queue : this.queues) {
      queue.leave(member);
    }
  }

  /**
   * Get the queue for certain ladder in a guild
   *
   * @param guild the guild to getId the queue from
   * @param ladder the ladder that needs the queue
   * @return the queue if exists else a new one will be created from {@link
   *     Ladder#createQueue(long)})}
   */
  @NonNull
  public MinecraftQueue getQueue(@NonNull GuidoGuild guild, @NonNull Ladder ladder) {
    for (MinecraftQueue queue : queues) {
      if (queue.getLadderName().equalsIgnoreCase(ladder.getName())) {
        return queue;
      }
    }
    MinecraftQueue queue = ladder.createQueue(guild.getId());
    this.queues.add(queue);
    return queue;
  }

  /**
   * Makes a member join a queue
   *
   * @param guild the guild where the queue is happening
   * @param member the member entering the queue
   * @param ladder the ladder to getId the queue of it
   * @return whether the member joined the queue
   */
  public QueueResult joinQueue(
      @NonNull GuidoGuild guild, @NonNull Member member, @NonNull Ladder ladder) {
    MinecraftQueue queue = this.getQueue(guild, ladder);
    DiscordLinkable discord = Discord.getUser(member);
    Optional<MinecraftLinkable> optional = runtime.getLinkableMatcher().getMinecraft(discord);
    if (optional.isEmpty()) return new QueueResult();
    QueueResult join = queue.join(optional.get());
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
  public Collection<MinecraftQueue> getQueues(@NonNull MinecraftLinkable info) {
    Set<MinecraftQueue> queues = new HashSet<>();
    for (MinecraftQueue queue : this.queues) {
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
   * @param ladder the ladder to getId the queue
   * @return true if the member is waiting in the queue
   */
  public boolean isWaiting(
      @NonNull GuidoGuild guild, @NonNull Member member, @NonNull Ladder ladder) {
    DiscordLinkable memberData = Discord.getUser(member);
    MinecraftQueue queue = this.getQueue(guild, ladder);
    Optional<MinecraftLinkable> optional = runtime.getLinkableMatcher().getMinecraft(memberData);
    if (optional.isEmpty()) return false;
    if (queue.isWaiting(optional.get())) {
      return true;
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
