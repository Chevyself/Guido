package me.googas.bot.core.handlers.matches;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.bot.api.events.match.MatchAddTeamEvent;
import me.googas.bot.api.events.match.MatchLoadedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.events.queue.QueueJoinEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.loader.BotDataLoader;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.Matches;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** This handles the match-making process for ranked matches */
// TODO this class and QueueHandler must separate its channel handlers to a different class
public class MatchMakingHandler implements GuidoHandler {

  // TODO move channel creation to MatchMakingChannelsHandler
  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NonNull MatchStatusUpdatedEvent event) {
    Match match = event.getMatch();
    BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(match.getGuildId());
    TextChannel channel = guildData.getTextChannel("matches");
    LocaleFile locale = Guido.getLanguageHandler().getDefault();
    EmbedQuery information = Matches.getInformation(match, locale);
    if (event.getStatus() == MatchStatus.FINISHED) {
      information.setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
      this.channels().deleteVoices(match);
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
    Match match = event.getQueue().checkReady();
    if (match != null) {
      new MatchLoadedEvent(match).call();
      for (LinkableInfo participant : match.getParticipants()) {
        Guido.getHandler(QueueHandler.class).leaveQueue(participant);
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
    Match match = event.getMatch();
    BotGuild guild = Matches.getGuild(match);
    if (guild == null) return;
    guild
        .getCategory("matches")
        .createVoiceChannel("Pre-Game " + match.getId())
        .queue(
            channel -> {
              this.channels().putPreMatch(channel.getIdLong(), match.getId());
              Discord.removeAllPermission(
                  channel,
                  Permission.VIEW_CHANNEL,
                  Permission.VOICE_SPEAK,
                  Permission.VOICE_STREAM,
                  Permission.VOICE_USE_VAD);
              for (LinkableInfo participant : match.getParticipants()) {
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
    Match match = event.getMatch();
    MatchTeam matchTeam = event.getMatchTeam();
    BotGuild data = Matches.getGuild(match);
    if (data != null) {
      data.getCategory("matches")
          .createVoiceChannel(matchTeam.getName())
          .queue(
              channel -> {
                this.channels()
                    .getVoices(match.getId())
                    .put(matchTeam.getId(), channel.getIdLong());
                Discord.removeAllPermission(
                    channel,
                    Permission.VIEW_CHANNEL,
                    Permission.VOICE_SPEAK,
                    Permission.VOICE_STREAM,
                    Permission.VOICE_USE_VAD);
                for (TeamMember member : matchTeam.getMembers()) {
                  Linkable link = member.getLinkInfo().getLink();
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
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamRemoveEvent(@NonNull MatchAddTeamEvent event) {
    Match match = event.getMatch();
    BotGuild data = Matches.getGuild(match);
    if (data != null) {
      Map<Integer, Long> voices = this.channels().getVoices(match.getId());
      VoiceChannel channel =
          Guido.getConnection()
              .validatedJda()
              .getVoiceChannelById(voices.getOrDefault(event.getMatchTeam().getId(), -1L));
      this.channels().deleteAndMove(data, channel);
      voices.remove(event.getMatchTeam().getId());
    }
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
  public Collection<Match> getPlaying(@NonNull UserData data) {
    BotDataLoader loader = Guido.getDataLoader();
    Collection<Linkable> links = loader.getLinks(data);
    Collection<Match> participating = new HashSet<>();
    for (Linkable link : links) {
      participating.addAll(
          loader.getParticipating(
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
  public void serverReady() {
    for (MatchHandler handler : Guido.getHandlers(MatchHandler.class)) {
      handler.serverReady();
    }
  }

  @Override
  public void close() {}

  @NonNull
  private MatchMakingChannelsHandler channels() {
    return Guido.getHandler(MatchMakingChannelsHandler.class);
  }
}
