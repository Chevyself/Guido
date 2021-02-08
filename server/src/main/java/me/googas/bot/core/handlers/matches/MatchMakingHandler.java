package me.googas.bot.core.handlers.matches;

import com.starfishst.commands.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.events.match.MatchAddTeamEvent;
import me.googas.api.events.match.MatchLoadedEvent;
import me.googas.api.events.match.MatchStatusUpdatedEvent;
import me.googas.api.events.queue.QueueJoinEvent;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.user.UserData;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.loader.GuidoLoader;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.Matches;
import me.googas.commons.Lots;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import me.googas.messaging.json.Receptor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** This handles the match-making process for ranked matches */
// TODO this class and QueueHandler must separate its channel handlers to a different class
public class MatchMakingHandler implements GuidoHandler {

  /** @see #onMatchStatusUpdatedEvent(MatchStatusUpdatedEvent) */
  @NonNull
  private static final Set<MatchStatus> announce =
      Lots.set(MatchStatus.STARTING, MatchStatus.FINISHED);

  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NonNull MatchStatusUpdatedEvent event) {
    if (!MatchMakingHandler.announce.contains(event.getStatus())) return;
    AbstractMatch abstractMatch = event.getAbstractMatch();
    GuidoGuild guildData =
        Guido.getHandlers().getDiscordLoader().getGuild(abstractMatch.getGuildId());
    TextChannel channel = guildData.getTextChannel("matches");
    LocaleFile locale = Guido.getHandlers().getLanguageHandler().getDefault();
    EmbedQuery information = Matches.getInformation(abstractMatch, locale);
    if (event.getStatus() == MatchStatus.FINISHED) {
      information.setTitle(
          locale.get("match.announce.title", Maps.singleton("id", abstractMatch.getId())));
      this.channels().deleteVoices(abstractMatch);
    }
    information.send(channel);
  }

  /**
   * Check if a match is ready when an link joins the queue
   *
   * @param event the event of a link joining a queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueJoin(QueueJoinEvent event) {
    AbstractMatch abstractMatch = event.getQueue().checkReady();
    if (abstractMatch != null) {
      new MatchLoadedEvent(abstractMatch).call();
      for (LinkableInfo participant : abstractMatch.getParticipants()) {
        Guido.getHandlers().getHandler(QueueHandler.class).leaveQueue(participant);
      }
    }
  }

  /**
   * When a match is loaded create a pre game channel
   *
   * @param event the event of a match being loaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchLoaded(MatchLoadedEvent event) {
    AbstractMatch abstractMatch = event.getAbstractMatch();
    GuidoGuild guild = Matches.getGuild(abstractMatch);
    guild
        .getCategory("matches")
        .createVoiceChannel("Pre-Game " + abstractMatch.getId())
        .queue(
            channel -> {
              this.channels().putPreMatch(channel.getIdLong(), abstractMatch.getId());
              Discord.removeAllPermission(
                  channel,
                  Permission.VIEW_CHANNEL,
                  Permission.VOICE_SPEAK,
                  Permission.VOICE_STREAM,
                  Permission.VOICE_USE_VAD);
              for (LinkableInfo participant : abstractMatch.getParticipants()) {
                Linkable link = participant.getLink();
                if (link == null) continue;
                Member member = link.requireDiscordRef().getMember(guild.toDiscord());
                if (member != null) {
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
                }
              }
            },
            Discord.exceptionConsumer());
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamAddTeamEvent(@NonNull MatchAddTeamEvent event) {
    AbstractMatch abstractMatch = event.getAbstractMatch();
    MatchTeam matchTeam = event.getMatchTeam();
    GuidoGuild data = Matches.getGuild(abstractMatch);
    data.getCategory("matches")
        .createVoiceChannel(matchTeam.getName())
        .queue(
            channel -> {
              this.channels()
                  .getVoices(abstractMatch.getId())
                  .put(matchTeam.getId(), channel.getIdLong());
              Discord.removeAllPermission(
                  channel,
                  Permission.VIEW_CHANNEL,
                  Permission.VOICE_SPEAK,
                  Permission.VOICE_STREAM,
                  Permission.VOICE_USE_VAD);
              for (TeamMember member : matchTeam.getMembers()) {
                Linkable link = member.getLink().getLink();
                if (link == null) continue;
                Member discordMember = link.requireDiscordRef().getMember(data.toDiscord());
                if (discordMember != null) {
                  Discord.addPermissions(
                      channel,
                      discordMember,
                      Discord.VOICE,
                      (aVoid -> {
                        GuildVoiceState state = discordMember.getVoiceState();
                        if (state != null) {
                          if (state.getChannel() != null) {
                            data.toDiscord()
                                .moveVoiceMember(discordMember, channel)
                                .queueAfter(500, TimeUnit.MILLISECONDS);
                          }
                        }
                      }));
                }
              }
            });
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamRemoveEvent(@NonNull MatchAddTeamEvent event) {
    AbstractMatch abstractMatch = event.getAbstractMatch();
    GuidoGuild data = Matches.getGuild(abstractMatch);
    Map<Integer, Long> voices = this.channels().getVoices(abstractMatch.getId());
    VoiceChannel channel =
        Guido.getConnection()
            .validatedJda()
            .getVoiceChannelById(voices.getOrDefault(event.getMatchTeam().getId(), -1L));
    this.channels().deleteAndMove(data, channel);
    voices.remove(event.getMatchTeam().getId());
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
  public Collection<AbstractMatch> getPlaying(@NonNull UserData data) {
    GuidoLoader loader = Guido.getHandlers().getLoader();
    Collection<Linkable> links = loader.getLinks().getLinks(data);
    Collection<AbstractMatch> participating = new HashSet<>();
    for (Linkable link : links) {
      participating.addAll(
          loader
              .getMatches()
              .getParticipating(
                  link.getType(),
                  link.getIdentification(),
                  MatchStatus.PLAYING,
                  MatchStatus.READY,
                  MatchStatus.STARTING,
                  MatchStatus.WAITING));
    }
    return participating;
  }

  /** Wake up queues waiting for server */
  @Receptor(Requests.MatchServer.SERVER_READY)
  public void serverReady() {
    for (MatchHandler handler : Guido.getHandlers().getHandlers(MatchHandler.class)) {
      handler.serverReady();
    }
  }

  @NonNull
  private MatchMakingChannelsHandler channels() {
    return Guido.getHandlers().getHandler(MatchMakingChannelsHandler.class);
  }

  @Override
  public boolean hasReceptors() {
    return true;
  }
}
