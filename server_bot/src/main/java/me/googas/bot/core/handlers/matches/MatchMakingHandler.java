package me.googas.bot.core.handlers.matches;

import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.events.match.*;
import me.googas.api.immutable.ImmutableLadder;
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
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.bot.core.util.Discord;
import me.googas.bot.core.util.Matches;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.server.GuidoGuild;
import me.googas.server.loader.GuidoLoader;
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
    TextChannel channel =
        runtime
            .getBotJda()
            .getGuidoGuild()
            .getMatchesTextChannel(runtime.getJdaConnection().getJda());
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
            .getMatchesCategory(runtime.getJdaConnection().getJda())
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
                              .toDiscord(runtime.getJdaConnection().getJda())
                              .moveVoiceMember(member, channel)
                              .queueAfter(500, TimeUnit.MILLISECONDS);
                        }
                      }
                    });
              });
    }
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchSetTeamsEvent(@NonNull MinecraftMatchSetTeamsEvent event) {
    for (MinecraftMatchTeam team : event.getTeams()) {
      this.createChannelForTeam(team, event.getMatch());
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

    this.createChannelForTeam(matchTeam, abstractMatch);
  }

  private void createChannelForTeam(MinecraftMatchTeam matchTeam, MinecraftMatch abstractMatch) {
    VoiceChannel channel =
        runtime
            .getBotJda()
            .getGuidoGuild()
            .getMatchesCategory(runtime.getJdaConnection().getJda())
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
    Optional<? extends MinecraftLinkable> minecraft =
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

  @Receptor(Requests.MinecraftMatches.SET_TEAMS)
  public Requests.SetTeamsData addTeam(
      @ParamName(Requests.MinecraftMatches.SET_TEAMS_MATCH_ID) UUID matchId,
      @ParamName(Requests.MinecraftMatches.SET_TEAMS_TEAMS) Requests.SetTeamsData data) {
    List<? extends MinecraftMatchTeam> result =
        runtime
            .getLoader()
            .getMinecraftMatches()
            .getById(matchId)
            .map(match -> match.setTeams(data.getTeams()))
            .orElseGet(ArrayList::new);
    return new Requests.SetTeamsData(result);
  }

  @Receptor(Requests.MinecraftMatches.UPDATE_STATUS)
  public void updateStatus(
      @ParamName(Requests.MinecraftMatches.UPDATE_STATUS_MATCH_ID) UUID matchId,
      @ParamName(Requests.MinecraftMatches.UPDATE_STATUS_STATUS) MatchStatus status) {
    runtime
        .getLoader()
        .getMinecraftMatches()
        .getById(matchId)
        .ifPresent(match -> match.setStatus(status));
  }

  @Receptor(Requests.MinecraftMatches.SET_MAP)
  public void setMap(
      @ParamName(Requests.MinecraftMatches.SET_MAP_MATCH_ID) UUID matchId,
      @ParamName(Requests.MinecraftMatches.SET_MAP_MAP_NAME) String mapName) {
    runtime
        .getLoader()
        .getMinecraftMatches()
        .getById(matchId)
        .ifPresent(match -> match.setMap(mapName));
  }

  @Receptor(Requests.MinecraftMatches.ON_FINISH)
  public void onFinish(
      @ParamName(Requests.MinecraftMatches.ON_FINISH_MATCH_ID) UUID matchId,
      @ParamName(Requests.MinecraftMatches.ON_FINISH_WINNERS_ID) int winnersId) {
    runtime
        .getLoader()
        .getMinecraftMatches()
        .getById(matchId)
        .ifPresent(match -> match.finish(winnersId));
  }

  @Receptor(Requests.MinecraftMatches.LADDER)
  public ImmutableLadder onFinish(
      @ParamName(Requests.MinecraftMatches.LADDER_NAME) String ladderName) {
    return runtime
        .getBotJda()
        .getGuidoGuild()
        .getLadder(ladderName)
        .map(
            ladder ->
                new ImmutableLadder(
                    ladder.playersPerTeam(),
                    ladder.baseValue(),
                    ladder.teamsPerMatch(),
                    ladder.getWinMultiplier(),
                    ladder.getLoseMultiplier(),
                    ladder.getName(),
                    ladder.getTeamSelectionType()))
        .orElse(null);
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
