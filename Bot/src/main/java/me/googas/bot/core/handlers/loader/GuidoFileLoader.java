package me.googas.bot.core.handlers.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkable;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.api.types.BotRole;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;

/**
 * This loader will attempt to get the data from files if it fails it will create a new instance of
 * the data required
 */
public class GuidoFileLoader implements BotDataLoader {
  /**
   * This will listen to when the guild data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onGuildDataUnloaded(@NonNull BotGuildUnloadedEvent event) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  /**
   * This will listen to when the role data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NonNull BotRoleUnloadedEvent event) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  /**
   * This will listen to when the user data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
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
  public @NonNull BotRole getRoleData(long id, long guildId) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  @Override
  public UserData getUserData(String id) {
    return null;
  }

  @Override
  public BotLinkable getLinkedData(@NonNull LinkableType type, @NonNull ValuesMap identifications) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public BotMatch getMatch(@NonNull String id) {
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
  public BotGroup getGroup(@NonNull String id) {
    throw new UnsupportedOperationException("Groups have not been implemented yet");
  }

  /**
   * Delete the group with the given id
   *
   * @param id the id of the group to delete
   * @return true if the group was deleted
   */
  @Override
  public boolean deleteGroup(String id) {
    return false;
  }

  /**
   * Count how many links there are {@link #getLinks(int, int, LinkableType...)}
   *
   * @param types the types of links to get
   * @return the amount of links
   */
  @Override
  public long countLinks(LinkableType... types) {
    return 0;
  }

  @Override
  public @NonNull Collection<Group> getGroups() {
    throw new UnsupportedOperationException("There's no groups");
  }

  /**
   * Get all the created groups but only the information of them
   *
   * @param page the page of groups to see
   * @param size the size of the groups per page
   * @return the created groups
   */
  @Override
  public @NonNull Collection<GroupInfo> getGroupsInfo(int page, int size) {
    return new HashSet<>();
  }

  /**
   * Get how many groups there are
   *
   * @return the amount of groups that there is
   */
  @Override
  public long countGroups() {
    return 0;
  }

  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull Ladder ladder, int page, int size) {
    return new HashMap<>();
  }

  /**
   * Get the leaderboard for certain stat
   *
   * @param stat the stat to look the leaderboard from
   * @param page the page to see of the leaderboard
   * @param size the size to show of the leaderboard
   * @param inverted whether the leaderboard should be inverted this means low to high
   * @return the leaderboard
   */
  @Override
  public @NonNull Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull String stat, int page, int size, boolean inverted) {
    return new HashMap<>();
  }

  /**
   * Get all the matches
   *
   * @param page the page of matches to see
   * @param size the size of the page
   * @param statuses the status of the matches to get
   * @return the collection of matches
   */
  @Override
  public @NonNull Collection<MatchInfo> getMatches(
      int page, int size, @NonNull MatchStatus... statuses) {
    return new HashSet<>();
  }

  @Override
  public @NonNull BotLinkable getDiscordUserData(long userId) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @NonNull BotLinkable getMemberData(long userId, long guildId) {
    throw new UnsupportedOperationException("Link data cannot be find using file loader");
  }

  @Override
  public @NonNull Collection<Linkable> getLinks(@NonNull UserData user) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @NonNull Collection<Linkable> getLinks(
      @NonNull UserData user, @NonNull LinkableType... types) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  /**
   * Get all the links that exist in the bot
   *
   * @param page the page to get of links
   * @param limit the amount of links per page
   * @param types the types of links to get
   * @return the collection of links
   */
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
  public @NonNull Collection<BotLinkable> getDiscordData(long userId) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public AuthToken getAuthToken(@NonNull String token) {
    throw new UnsupportedOperationException("Auth tokens cannot be find using file loader");
  }
}
