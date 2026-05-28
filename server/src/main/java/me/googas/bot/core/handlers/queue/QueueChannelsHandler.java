package me.googas.bot.core.handlers.queue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.events.queue.QueueLeaveEvent;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.api.matches.queue.Queueable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
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

  /** The waiting channel for the users in queue */
  @NonNull private final Map<Long, Long> waiting = new HashMap<>();

  /**
   * Check when a member leaves a voice channel. If the voice channel is the queues waiting channel
   * then remove them from the queue
   *
   * @param event the event of a member leaving a voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceMove(@NonNull GuildVoiceUpdateEvent event) {
    this.checkRemoveQueue(event.getChannelLeft(), event.getGuild(), event.getMember());
    this.queues()
        .joinQueueFromVoice(
            event.getGuild().getIdLong(), event.getChannelJoined(), event.getMember());
  }

  /**
   * Check if the user that left the channel may leave the queue
   *
   * @param channelLeft the channel that the user left
   * @param guild the guild in which the event happened
   * @param discordMember the event that left the queue
   */
  public void checkRemoveQueue(
      @NonNull AudioChannelUnion channelLeft, @NonNull Guild guild, @NonNull Member discordMember) {
    long guildId = guild.getIdLong();
    long channelId = this.waiting.getOrDefault(guildId, -1L);
    if (channelId == channelLeft.getIdLong()) {
      DiscordLinkable member = Discord.getUser(discordMember.getIdLong());
      for (Linkable data : member.getLinks()) {
        this.queues().leaveQueue(data.getInfo());
      }
      this.checkDeletion(channelLeft, guildId);
    }
  }

  /**
   * Check whether the waiting channel should be deleted
   *
   * @param channel the channel which is being checked whether to delete it
   * @param guildId the id of the guild
   */
  private void checkDeletion(@NonNull AudioChannelUnion channel, long guildId) {
    List<Member> members = channel.getMembers();
    if (members.isEmpty()) {
      this.waiting.remove(guildId);
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
          AudioChannelUnion channel = voiceState.getChannel();
          if (channel != null
              && channel.getIdLong()
                  == this.waiting.getOrDefault(event.getQueue().getGuildId(), -1L)) {
            member.getGuild().moveVoiceMember(member, null).queue();
          }
        }
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
  public VoiceChannel getWaitingChannel(GuidoGuild guild) {
    long id =
        this.waiting.computeIfAbsent(
            guild.getId(),
            aLong ->
                guild.getCategory("matches").createVoiceChannel("Queue").complete().getIdLong());
    VoiceChannel channel = guild.toDiscord().getVoiceChannelById(id);
    if (channel == null) {
      this.waiting.remove(guild.getId());
      return this.getWaitingChannel(guild);
    } else {
      return channel;
    }
  }

  @Override
  public void onDisable() {}
}
