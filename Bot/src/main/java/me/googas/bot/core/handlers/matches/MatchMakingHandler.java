package me.googas.bot.core.handlers.matches;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.googas.api.discord.GuildData;
import me.googas.api.lang.LocaleFile;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamMember;
import me.googas.api.user.UserData;
import me.googas.bot.api.events.match.MatchAddTeamEvent;
import me.googas.bot.api.events.match.MatchLoadedEvent;
import me.googas.bot.api.events.match.MatchStatusUpdatedEvent;
import me.googas.bot.api.events.queue.QueueJoinEvent;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.GuidoEventHandler;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.console.Console;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/** This handles the match-making process for ranked matches */
public class MatchMakingHandler implements GuidoEventHandler {

  /**
   * A map that contains the team voice channels in a match First element in the map is the id of
   * the match and then another map with the id of the team and the id of the voice channel
   */
  @NotNull private final Map<String, Map<Integer, Long>> teamsVoices = new HashMap<>();

  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NotNull MatchStatusUpdatedEvent event) {
    Match match = event.getMatch();
    Console.debug(match + " has updated its event to " + event.getStatus());
    if (event.getStatus() == MatchStatus.FINISHED) {
      LocaleFile locale = Guido.getLanguageHandler().getDefault();
      long guildId = match.getGuildId();
      BotGuild guildData = Guido.getDataLoader().getGuildDataOrCreate(guildId);
      if (match instanceof BotMatch) {
        TextChannel channel = guildData.getChannel("matches");
        EmbedQuery information = ((BotMatch) match).getInformation(locale);
        information
            .getEmbedBuilder()
            .setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
        information.send(channel);
        Console.debug(match + " has been announced in " + channel);
      }
      Map<Integer, Long> voices = this.getVoices(match.getId());
      for (Long value : voices.values()) {
        VoiceChannel channel = Guido.getConnection().validatedJda().getVoiceChannelById(value);
        this.deleteAndMove(guildData, channel);
      }
      this.teamsVoices.remove(match.getId());
    }
  }

  /**
   * Check if a match is ready when an link joins the queue
   *
   * @param event the event of a link joining a queue
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onQueueJoin(QueueJoinEvent event) {
    Match match = event.getQueue().checkReady();
    Console.debug("Is the queue " + event.getQueue() + " ready to create a match? " + match);
    if (match != null) {
      new MatchLoadedEvent(match).call();
      for (LinkableInfo participant : match.getParticipants()) {
        Guido.getHandler(QueueHandler.class).leaveQueue(participant);
      }
    }
  }

  /**
   * Listen to when a team is added to a match to create a voice channel for it
   *
   * @param event the event of a team being added to a match
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamAddTeamEvent(@NotNull MatchAddTeamEvent event) {
    Match match = event.getMatch();
    Team team = event.getTeam();
    GuildData data = match.getGuildData();
    if (data instanceof BotGuild) {
      ((BotGuild) data)
          .getCategory("matches")
          .createVoiceChannel(team.getName())
          .queue(
              channel -> {
                this.getVoices(match.getId()).put(team.getId(), channel.getIdLong());
                Discord.removeAllPermission(channel);
                for (TeamMember member : team.getMembers()) {
                  LinkableData link = member.getLinkInfo().getLink();
                  if (link instanceof BotLinkableData) {
                    Member discordMember = ((BotLinkableData) link).getDiscordMember(data.getId());
                    if (discordMember != null) {
                      Discord.addPermissions(
                          channel,
                          discordMember,
                          Discord.VOICE,
                          (aVoid -> {
                            GuildVoiceState state = discordMember.getVoiceState();
                            if (state != null) {
                              if (state.getChannel() != null) {
                                ((BotGuild) data)
                                    .getDiscord()
                                    .moveVoiceMember(discordMember, channel)
                                    .queue();
                              }
                            }
                          }));
                    }
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
  public void onTeamRemoveEvent(@NotNull MatchAddTeamEvent event) {
    Match match = event.getMatch();
    GuildData data = match.getGuildData();
    if (data instanceof BotGuild) {
      BotGuild botGuild = (BotGuild) data;
      Map<Integer, Long> voices = this.getVoices(match.getId());
      VoiceChannel channel =
          Guido.getConnection()
              .validatedJda()
              .getVoiceChannelById(voices.getOrDefault(event.getTeam().getId(), -1L));
      this.deleteAndMove(botGuild, channel);
      voices.remove(event.getTeam().getId());
    }
  }

  /**
   * Delete and move all the members from a team voice channel
   *
   * @param botGuild the guild where the channel exists
   * @param channel the channel itself
   */
  public void deleteAndMove(BotGuild botGuild, VoiceChannel channel) {
    VoiceChannel waiting = botGuild.getVoiceChannel("waiting");
    if (channel != null) {
      for (Member member : channel.getMembers()) {
        botGuild.getDiscord().moveVoiceMember(member, waiting).queue();
      }
      int time = 0;
      while (!channel.getMembers().isEmpty()) {
        time++;
        if (time > 3000) {
          break;
        }
      }
      channel.delete().queue();
    }
  }

  /**
   * Get whether an user is playing
   *
   * @param data the user to check if playing
   * @return true if the user is playing
   */
  public boolean isPlaying(@NotNull UserData data) {
    return !this.getPlaying(data).isEmpty();
  }

  /**
   * Get whether an user is playing
   *
   * @param info the user to check if playing
   * @return true if the user is playing
   */
  @Deprecated
  public boolean isPlaying(@NotNull LinkableInfo info) {
    LinkableData link = info.getLink();
    if (link != null) {
      UserData user = link.getLinkedUser();
      if (user != null) {
        return this.getPlaying(user).isEmpty();
      } else {
        return this.getPlaying(info).isEmpty();
      }
    }
    return false;
  }

  /**
   * Get where an user is playing
   *
   * @param data the user to check where it is playing
   * @return the collection of matches where the user is playing
   */
  public Collection<Match> getPlaying(@NotNull UserData data) {
    BotDataLoader loader = Guido.getDataLoader();
    Collection<LinkableData> links = loader.getLinks(data);
    Collection<Match> participating = new HashSet<>();
    for (LinkableData link : links) {
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

  /**
   * Get where a link is playing
   *
   * @param info the link to check where it is playing
   * @return the collection of matches where the user is playing
   */
  public Collection<Match> getPlaying(@NotNull LinkableInfo info) {
    BotDataLoader loader = Guido.getDataLoader();
    return loader.getParticipating(
        info.getType(),
        info.getIdentification(),
        MatchStatus.PLAYING,
        MatchStatus.READY,
        MatchStatus.STARTING,
        MatchStatus.WAITING);
  }

  /**
   * Get the voices channels for the teams in a match
   *
   * @param matchId the id of the match
   * @return the voices channels for the teams in a match
   */
  private Map<Integer, Long> getVoices(@NotNull String matchId) {
    return this.teamsVoices.computeIfAbsent(matchId, k -> new HashMap<>());
  }

  /**
   * Get the voices channels for the teams in a match
   *
   * @param match the match to id and the channels
   * @return the voices channels for the teams in a match
   */
  public Map<Integer, Long> getVoices(@NotNull Match match) {
    return this.getVoices(match.getId());
  }

  @Override
  public void close() {}
}
