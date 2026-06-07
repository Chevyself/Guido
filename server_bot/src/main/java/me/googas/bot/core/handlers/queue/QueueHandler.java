package me.googas.bot.core.handlers.queue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.matches.ladder.PlayableLadder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

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
   * @param channelJoined the channel that the member joined
   * @param member the member that joined the channel
   */
  public void joinQueueFromVoice(AudioChannelUnion channelJoined, @NonNull Member member) {
    if (channelJoined == null) return;
    /*TODO
    GuidoGuild guild = runtime.getBotJda().getGuidoGuild();
    String key = guild.getVoiceChannel(channelJoined.getIdLong());
    if (key != null && key.startsWith("join-")) {
      String ladderName = key.substring(5);
      guild.getLadder(ladderName)
              .ifPresent(ladder -> {
                QueueResult result = this.joinQueue(guild, member, ladder);
                if (result.isCancelled())
                  member
                          .getUser()
                          .openPrivateChannel()
                          .queue(
                                  channel -> {
                                    channel.sendMessage(result.getReason()).queue();
                                  });
              });
    }*/
  }

  /*
  @SubscribeEvent
  public void onGuildVoiceJoinEvent(@NonNull GuildVoiceUpdateEvent event) {
    this.joinQueueFromVoice(event.getChannelJoined(), event.getMember());
  }*/

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
   * @param ladder the ladder that needs the queue
   * @return the queue if exists else a new one will be created from {@link
   *     PlayableLadder#createQueue(GuidoBotRuntime)}}
   */
  @NonNull
  public MinecraftQueue getQueue(@NonNull PlayableLadder ladder) {
    for (MinecraftQueue queue : queues) {
      if (queue.getLadderName().equalsIgnoreCase(ladder.getName())) {
        return queue;
      }
    }
    MinecraftQueue queue = ladder.createQueue(runtime);
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
      @NonNull GuidoGuild guild, @NonNull Member member, @NonNull PlayableLadder ladder) {
    MinecraftQueue queue = this.getQueue(ladder);
    Optional<MinecraftLinkable> optional =
        runtime
            .getLoader()
            .getDiscordLinks()
            .ensureByMember(member)
            .getLinkedUserId()
            .flatMap(
                linkedUserId ->
                    runtime.getLoader().getMinecraftLinks().getByLinkedUser(linkedUserId));
    if (optional.isEmpty()) return new QueueResult();
    QueueResult join = queue.join(optional.get());
    if (join.isCancelled()) return join;
    guild
        .toDiscord()
        .moveVoiceMember(member, this.channels().getWaitingChannel(guild, guild.toDiscord()))
        .queue();
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
   * @param member the member to check
   * @param ladder the ladder to getId the queue
   * @return true if the member is waiting in the queue
   */
  public boolean isWaiting(@NonNull Member member, @NonNull PlayableLadder ladder) {
    MinecraftQueue queue = this.getQueue(ladder);
    Optional<MinecraftLinkable> optional =
        runtime
            .getLoader()
            .getDiscordLinks()
            .ensureByMember(member)
            .getLinkedUserId()
            .flatMap(
                linkedUserId ->
                    runtime.getLoader().getMinecraftLinks().getByLinkedUser(linkedUserId));
    return optional.filter(queue::isWaiting).isPresent();
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
