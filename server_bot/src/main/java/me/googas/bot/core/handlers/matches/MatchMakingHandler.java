package me.googas.bot.core.handlers.matches;

import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.events.match.MinecraftMatchAddTeamEvent;
import me.googas.api.events.match.MinecraftMatchLoadedEvent;
import me.googas.api.events.match.MinecraftMatchRemoveTeamEvent;
import me.googas.api.events.match.MinecraftMatchStatusUpdatedEvent;
import me.googas.api.events.queue.MinecraftQueueJoinEvent;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.minecraft.MinecraftMatchTeamMember;
import me.googas.api.user.UserData;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.Matches;
import me.googas.net.sockets.json.Receptor;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/** This handles the match-making process for ranked matches */
// TODO this class and QueueHandler must separate its channel handlers to a different class
public class MatchMakingHandler implements GuidoHandler {

  @NonNull private final GuidoBotRuntime runtime;

  /** @see #onMatchStatusUpdatedEvent(MinecraftMatchStatusUpdatedEvent) */
  @NonNull
  private static final Set<MatchStatus> announce =
      Lots.set(MatchStatus.STARTING, MatchStatus.FINISHED);

  public MatchMakingHandler(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NonNull MinecraftMatchStatusUpdatedEvent event) {
    if (!MatchMakingHandler.announce.contains(event.getStatus())) return;
    MinecraftMatch match = event.getMatch();
    TextChannel channel = runtime.getBotJda().getGuidoGuild().getMatchesTextChannel();
    LocaleFile locale = Guido.getHandlers().getLanguageHandler().getDefault();
    EmbedBuilder information = Matches.getInformation(match, locale, runtime.getLoader());
    if (event.getStatus() == MatchStatus.FINISHED) {
      information.setTitle(
          locale.get("match.announce.title", Maps.singleton("id", match.getId().toString())));
      this.channels().deleteVoices(match);
    }
    channel.sendMessageEmbeds(information.build()).queue();
  }

  /**
   * Check if a match is ready when an link joins the queue
   *
   * @param event the event of a link joining a queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueJoin(MinecraftQueueJoinEvent event) {
    Optional<MinecraftMatch> optional = event.getQueue().checkReady();
    if (optional.isEmpty()) return;
    MinecraftMatch match = optional.get();
    runtime.getListeners().call(new MinecraftMatchLoadedEvent(match));
    for (MinecraftMatchTeamMember participant : match.getParticipants()) {
      Guido.getHandlers().getHandler(QueueHandler.class).leaveQueue(participant);
    }
  }

  /**
   * When a match is loaded create a pre game channel
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(MinecraftMatchLoadedEvent event) {
    MinecraftMatch minecraftMatch = event.getMatch();
    GuidoGuild guild = runtime.getBotJda().getGuidoGuild();
    VoiceChannel channel =
        guild
            .getMatchesCategory()
            .createVoiceChannel("Pre-Game " + minecraftMatch.getId())
            .complete();
    this.channels().putPreMatch(channel.getIdLong(), minecraftMatch.getId());
    Discord.removeAllPermission(
        channel,
        Permission.VIEW_CHANNEL,
        Permission.VOICE_SPEAK,
        Permission.VOICE_STREAM,
        Permission.VOICE_USE_VAD);
    for (MinecraftMatchTeamMember participant : minecraftMatch.getParticipants()) {
      participant
          .getLinkable(runtime.getLoader())
          .flatMap(Linkable::getLinkedUserId)
          .flatMap(
              linkedUserId -> runtime.getLoader().getDiscordLinks().getByLinkedUser(linkedUserId))
          .flatMap(discord -> discord.getMember(runtime.getBotJda()))
          .ifPresent(
              member -> {
                Discord.addPermissions(
                    channel,
                    member,
                    Discord.VOICE,
                    aVoid -> {
                      GuildVoiceState state = member.getVoiceState();
                      if (state != null) {
                        if (state.getChannel() != null) {
                          guild
                              .toDiscord()
                              .moveVoiceMember(member, channel)
                              .queueAfter(500, TimeUnit.MILLISECONDS);
                        }
                      }
                    });
              });
    }
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamAddTeamEvent(@NonNull MinecraftMatchAddTeamEvent event) {
    MinecraftMatch abstractMatch = event.getMatch();
    MinecraftMatchTeam matchTeam = event.getTeam();

    VoiceChannel channel =
        runtime
            .getBotJda()
            .getGuidoGuild()
            .getMatchesCategory()
            .createVoiceChannel(matchTeam.getName())
            .complete();
    this.channels().getVoices(abstractMatch.getId()).put(matchTeam.getId(), channel.getIdLong());
    Discord.removeAllPermission(
        channel,
        Permission.VIEW_CHANNEL,
        Permission.VOICE_SPEAK,
        Permission.VOICE_STREAM,
        Permission.VOICE_USE_VAD);
    for (MinecraftMatchTeamMember member : matchTeam.getMembers()) {
      member
          .getLinkable(runtime.getLoader())
          .flatMap(Linkable::getLinkedUserId)
          .flatMap(
              linkedUserId -> runtime.getLoader().getDiscordLinks().getByLinkedUser(linkedUserId))
          .flatMap(discord -> discord.getMember(runtime.getBotJda()))
          .ifPresent(
              discordMember -> {
                Discord.addPermissions(
                    channel,
                    discordMember,
                    Discord.VOICE,
                    (aVoid -> {
                      GuildVoiceState state = discordMember.getVoiceState();
                      if (state != null) {
                        if (state.getChannel() != null) {
                          runtime
                              .getBotJda()
                              .getGuild()
                              .moveVoiceMember(discordMember, channel)
                              .queueAfter(500, TimeUnit.MILLISECONDS);
                        }
                      }
                    }));
              });
    }
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamRemoveEvent(@NonNull MinecraftMatchRemoveTeamEvent event) {
    MinecraftMatch abstractMatch = event.getMatch();
    Map<Integer, Long> voices = this.channels().getVoices(abstractMatch.getId());
    MinecraftMatchTeam team = event.getTeam();
    VoiceChannel channel =
        runtime
            .getJdaConnection()
            .getJda()
            .getVoiceChannelById(voices.getOrDefault(team.getId(), -1L));
    this.channels().deleteAndMove(channel);
    voices.remove(team.getId());
  }

  /**
   * Get whether an user is playing
   *
   * @param data the user to check if playing
   * @return true if the user is playing
   */
  public boolean isPlaying(@NonNull UserData data) {
    return !this.getPlaying(data).isEmpty();
  }

  /**
   * Get where an user is playing
   *
   * @param data the user to check where it is playing
   * @return the collection of matches where the user is playing
   */
  public Collection<MinecraftMatch> getPlaying(@NonNull UserData data) {
    GuidoLoader loader = runtime.getLoader();
    Optional<MinecraftLinkable> minecraft =
        runtime.getLoader().getMinecraftLinks().getByLinkedUser(data.getId());
    Collection<MinecraftMatch> participating = new HashSet<>();
    if (minecraft.isEmpty()) return participating;
    participating.addAll(
        loader
            .getMinecraftMatches()
            .getParticipating(
                minecraft.get().getId(),
                MatchStatus.PLAYING,
                MatchStatus.READY,
                MatchStatus.STARTING,
                MatchStatus.WAITING));
    return participating;
  }

  /** Wake up queues waiting for server */
  @Receptor(Requests.MatchServer.SERVER_READY)
  public void serverReady() {
    for (MatchHandler handler : runtime.getHandlers().getHandlers(MatchHandler.class)) {
      handler.serverReady();
    }
  }

  @NonNull
  private MatchMakingChannelsHandler channels() {
    return runtime.getHandlers().getHandler(MatchMakingChannelsHandler.class);
  }

  @Override
  public boolean hasReceptors() {
    return true;
  }
}
