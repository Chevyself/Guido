package me.googas.bot.core.handlers.queue;

import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.events.queue.MinecraftQueueLeaveEvent;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** Handles the queue channels */
public class QueueChannelsHandler implements GuidoHandler {

  @NonNull @Getter private final GuidoBotRuntime runtime;

  /** The waiting channel for the users in queue */
  private long waiting = -1;

  public QueueChannelsHandler(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  /**
   * Check when a member leaves a voice channel. If the voice channel is the queues waiting channel
   * then remove them from the queue
   *
   * @param event the event of a member leaving a voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceMove(@NonNull GuildVoiceUpdateEvent event) {
    this.checkRemoveQueue(event.getChannelLeft(), event.getGuild(), event.getMember());
    this.queues().joinQueueFromVoice(event.getChannelJoined(), event.getMember());
  }

  /**
   * Check if the user that left the channel may leave the queue
   *
   * @param channelLeft the channel that the user left
   * @param guild the guild in which the event happened
   * @param discordMember the event that left the queue
   */
  public void checkRemoveQueue(
      AudioChannelUnion channelLeft, @NonNull Guild guild, @NonNull Member discordMember) {
    if (channelLeft == null) return;
    long guildId = guild.getIdLong();
    if (this.waiting == channelLeft.getIdLong()) {
      runtime
          .getLoader()
          .getDiscordLinks()
          .ensureByUser(discordMember.getUser())
          .getLinkedUserId()
          .flatMap(
              linkedUserId -> runtime.getLoader().getMinecraftLinks().getByLinkedUser(linkedUserId))
          .ifPresent(minecraft -> this.queues().leaveQueue(minecraft));
      this.checkDeletion(channelLeft, guildId);
    }
  }

  /**
   * Check whether the waiting channel should be deleted
   *
   * @param channel the channel which is being checked whether to delete it
   * @param guildId the id of the guild
   */
  private void checkDeletion(AudioChannelUnion channel, long guildId) {
    if (channel == null) return;
    List<Member> members = channel.getMembers();
    if (members.isEmpty()) {
      this.waiting = -1;
      channel.delete().queue();
    }
  }

  /**
   * Get the queue handler
   *
   * @return the queue handler
   */
  @NonNull
  private QueueHandler queues() {
    return Guido.getHandlers().getHandler(QueueHandler.class);
  }

  /**
   * When an user leaves the queue make then leave the voice channel
   *
   * @param event the event of an user leaving the queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueLeave(MinecraftQueueLeaveEvent event) {
    Optional<Member> optional =
        event
            .getMinecraft()
            .getLinkedUserId()
            .flatMap(
                linkedUserId -> runtime.getLoader().getDiscordLinks().getByLinkedUser(linkedUserId))
            .flatMap(discord -> discord.getMember(runtime.getBotJda()));
    if (optional.isEmpty()) return;
    Member member = optional.get();
    GuildVoiceState voiceState = member.getVoiceState();
    if (voiceState != null) {
      AudioChannelUnion channel = voiceState.getChannel();
      if (channel != null && channel.getIdLong() == this.waiting) {
        member.getGuild().moveVoiceMember(member, null).queue();
      }
    }
  }

  /**
   * Get the waiting channel for a guild
   *
   * @param guild the guild waiting for a channel
   * @return the channel
   */
  @NonNull
  public VoiceChannel getWaitingChannel(GuidoGuild guildData, Guild guild) {
    VoiceChannel channel = null;
    if (this.waiting != -1) {
      channel = guild.getVoiceChannelById(this.waiting);
    }
    if (channel == null) {
      channel = guildData.getMatchesCategory().createVoiceChannel("Queue").complete();
      this.waiting = channel.getIdLong();
    }
    return channel;
  }

  @Override
  public void onDisable() {}
}
