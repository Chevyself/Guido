package me.googas.bot.core.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.discord.BotRole;
import me.googas.bot.api.types.loader.BotDataLoader;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;

/**
 * This loader will attempt to get the data from files if it fails it will create a new instance of
 * the data required
 */
public class GuidoFileLoader implements BotDataLoader {

  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NonNull BotGuildUnloadedEvent event) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onTeamDataUnloaded(@NonNull TeamDataUnloadedEvent event) {
    throw new UnsupportedOperationException("MatchTeam data cannot be find using file loader");
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NonNull BotRoleUnloadedEvent event) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  @Listener(priority = ListenPriority.HIGHEST)
  public void onUserDataUnloaded(@NonNull UserUnloadedDataEvent event) {
    throw new UnsupportedOperationException("User data cannot be find using file loader");
  }

  @Override
  public void close() {}

  @Override
  public @NonNull BotGuild getGuildDataOrCreate(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public BotGuild getGuildData(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public Linkable getLink(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition) {
    return null;
  }

  @Override
  public Linkable getLinkByRecognition(@NonNull LinkableType type, @NonNull ValuesMap recognition) {
    return null;
  }

  @Override
  public @NonNull BotRole getRoleData(long id, long guildId) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  @Override
  public UserData getUserData(String id) {
    return null;
  }

  @Override
  public Linkable getLink(@NonNull LinkableType type, @NonNull ValuesMap identifications) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public Match getMatch(@NonNull String id) {
    return null;
  }

  @Override
  public long maxPageLeaderboard(@NonNull Ladder ladder, int size) {
    return 0;
  }

  /**
   * Get the max page of the leaderboard in a stat
   *
   * @param stat the stat to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  @Override
  public long maxPageLeaderboard(@NonNull String stat, int size) {
    return 0;
  }

  @Override
  public @NonNull Collection<Match> getParticipating(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull MatchStatus... status) {
    return new ArrayList<>();
  }

  @Override
  public Punishment getPunishment(@NonNull String id) {
    return null;
  }

  @Override
  public Group getGroup(@NonNull String id) {
    throw new UnsupportedOperationException("Groups have not been implemented yet");
  }

  @Override
  public boolean deleteGroup(String id) {
    return false;
  }

  @Override
  public long countLinks(LinkableType... types) {
    return 0;
  }

  @Override
  public @NonNull Collection<GroupInfo> getGroups(int page, int size) {
    return new HashSet<>();
  }

  @Override
  public @NonNull Collection<Group> getGroups() {
    throw new UnsupportedOperationException("There's no groups");
  }

  @Override
  public Team getTeam(@NonNull String id) {
    return null;
  }

  @Override
  public Team getTeamByName(@NonNull String name) {
    return null;
  }

  @Override
  public Team getTeam(@NonNull Linkable linkable) {
    return null;
  }

  @Override
  public long maxPageGroups(int size) {
    return 0;
  }

  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull Ladder ladder, int page, int size) {
    return new HashMap<>();
  }

  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull String stat, int page, int size, boolean inverted) {
    return new HashMap<>();
  }

  @Override
  public @NonNull Collection<MatchInfo> getMatches(
      int page, int size, @NonNull MatchStatus... statuses) {
    return new HashSet<>();
  }

  @Override
  public boolean deleteTeam(@NonNull String id) {
    return false;
  }

  @Override
  public @NonNull Linkable getDiscordUserData(long userId) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @NonNull Collection<Linkable> getLinks(@NonNull UserData user) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public Linkable getLink(@NonNull UserData user, @NonNull LinkableType type) {
    return null;
  }

  @Override
  public Collection<LinkableInfo> getLinks(int page, int limit, @NonNull LinkableType... types) {
    return new ArrayList<>();
  }

  @NonNull
  @Override
  public Collection<AuthToken> getTokens(@NonNull UserData user) {
    throw new UnsupportedOperationException("File loader cannot get tokens");
  }

  @Override
  public @NonNull Collection<Linkable> getDiscordData(long userId) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public AuthToken getAuthToken(@NonNull String token) {
    throw new UnsupportedOperationException("Auth tokens cannot be find using file loader");
  }
}
