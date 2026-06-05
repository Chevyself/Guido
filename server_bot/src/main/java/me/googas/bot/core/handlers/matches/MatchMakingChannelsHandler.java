package me.googas.bot.core.handlers.matches;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class MatchMakingChannelsHandler implements GuidoHandler {

  // TODO create a map or a set of channels that must be deleted
  /**
   * A map that contains the team voice channels in a match First element in the map is the id of
   * the match and then another map with the id of the team and the id of the voice channel
   */
  @NonNull private final Map<UUID, Map<Integer, Long>> teams = new HashMap<>();

  /** The pre match channels created */
  @NonNull private final Map<Long, UUID> preMatch = new HashMap<>();

  /**
   * Get the voices channels for the teams in a match
   *
   * @param matchId the id of the match
   * @return the voices channels for the teams in a match
   */
  public Map<Integer, Long> getVoices(@NonNull UUID matchId) {
    return this.teams.computeIfAbsent(matchId, k -> new HashMap<>());
  }

  /**
   * Get the voices channels for the teams in a abstractMatch
   *
   * @param abstractMatch the abstractMatch to id and the channels
   * @return the voices channels for the teams in a abstractMatch
   */
  public Map<Integer, Long> getVoices(@NonNull MinecraftMatch abstractMatch) {
    return this.getVoices(abstractMatch.getId());
  }

  /**
   * Check if the channel is a pregame if so delete it
   *
   * @param channel the channel to check
   */
  public void checkDeletePreGame(AudioChannelUnion channel) {
    if (channel == null) return;
    UUID id = this.preMatch.get(channel.getIdLong());
    if (id == null) return;
    if (channel.getMembers().isEmpty()) {
      channel.delete().queue();
    }
  }

  /**
   * Listen to when a member moves from voice channel to check if it is a pregame channel to delete
   * it
   *
   * @param event the event of a member moving from voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceMove(@NonNull GuildVoiceUpdateEvent event) {
    this.checkDeletePreGame(event.getChannelLeft());
  }

  /**
   * Listen to when a member leaves a voice channel to check if it is a pregame channel to delete it
   *
   * @param event the event of a member leaving a voice channel
   */
  @SubscribeEvent
  public void onGuildVoiceLeave(@NonNull GuildVoiceUpdateEvent event) {
    this.checkDeletePreGame(event.getChannelLeft());
  }

  /**
   * Put a new pre match channel in the pre match map
   *
   * @param idLong the id of the pre match channel
   * @param id the id of the match
   */
  public void putPreMatch(long idLong, @NonNull UUID id) {
    this.preMatch.put(idLong, id);
  }

  /**
   * Remove the team voices channel
   *
   * @param id the id of the match to remove the team voices channels
   */
  public void removeTeam(@NonNull String id) {
    this.teams.remove(id);
  }

  /**
   * Delete and move all the members from a team voice channel
   *
   * @param botGuild the guild where the channel exists
   * @param channel the channel itself
   */
  public void deleteAndMove(VoiceChannel channel) {
    VoiceChannel waiting = botGuild.getVoiceChannel("waiting");
    if (channel != null) {
      for (Member member : channel.getMembers()) {
        if (member.getVoiceState() != null && member.getVoiceState().getChannel() != null) {
          botGuild
              .toDiscord()
              .moveVoiceMember(member, waiting)
              .queueAfter(
                  500,
                  TimeUnit.MILLISECONDS,
                  aVoid -> {
                    if (channel.getMembers().isEmpty()) {
                      channel.delete().queue(ignored -> {}, Discord.exceptionConsumer());
                    }
                  },
                  Discord.exceptionConsumer());
        }
      }
    }
  }

  /**
   * Delete the voice channels from a abstractMatch
   *
   * @param abstractMatch the abstractMatch to delete the voice channels
   */
  public void deleteVoices(@NonNull MinecraftMatch abstractMatch) {
    Map<Integer, Long> voices = this.getVoices(abstractMatch.getId());
    for (Long value : voices.values()) {
      VoiceChannel voiceChannel = Guido.getConnection().validatedJda().getVoiceChannelById(value);
      this.deleteAndMove(voiceChannel);
    }
    this.teams.remove(abstractMatch.getId());
  }

  @Override
  public void onDisable() {}
}
