package me.googas.bot.core.handlers.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableDataType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.permissions.Group;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.events.data.role.BotRoleUnloadedEvent;
import me.googas.bot.api.events.data.user.UserUnloadedDataEvent;
import me.googas.bot.api.loader.BotDataLoader;
import me.googas.bot.api.types.BotGroup;
import me.googas.bot.api.types.BotGuild;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.api.types.BotMatch;
import me.googas.bot.api.types.BotRole;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
  public void onGuildDataUnloaded(@NotNull BotGuildUnloadedEvent event) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  /**
   * This will listen to when the role data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onRoleDataUnloaded(@NotNull BotRoleUnloadedEvent event) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  /**
   * This will listen to when the user data gets unloaded to save it
   *
   * @param event the event of the data being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onUserDataUnloaded(@NotNull UserUnloadedDataEvent event) {
    throw new UnsupportedOperationException("User data cannot be find using file loader");
  }

  @Override
  public void close() {}

  @Override
  public @NotNull BotGuild getGuildDataOrCreate(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public @Nullable BotGuild getGuildData(long id) {
    throw new UnsupportedOperationException("Guild data cannot be find using file loader");
  }

  @Override
  public @NotNull BotRole getRoleData(long id, long guildId) {
    throw new UnsupportedOperationException("Role data cannot be find using file loader");
  }

  @Override
  public @Nullable UserData getUserData(@Nullable String id) {
    return null;
  }

  @Override
  public @Nullable BotLinkableData getLinkedData(
      @NotNull LinkableDataType type, @NotNull ValuesMap identifications, boolean equal) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @Nullable BotMatch getMatch(@NotNull String id) {
    return null;
  }

  @Override
  public long maxPageLeaderboard(@NotNull Ladder ladder, int size) {
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
  public long maxPageLeaderboard(@NotNull String stat, int size) {
    return 0;
  }

  @Override
  public @NotNull Collection<Match> getParticipating(
      @NotNull LinkableDataType type,
      @NotNull ValuesMap identification,
      @NotNull MatchStatus... status) {
    return new ArrayList<>();
  }

  @Override
  public @Nullable BotGroup getGroup(@NotNull String id) {
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

  @Override
  public @NotNull Collection<Group> getGroups() {
    throw new UnsupportedOperationException("There's no groups");
  }

  @Override
  public @NotNull Map<Integer, LinkableData> getLeaderboard(
      @NotNull Ladder ladder, int page, int size) {
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
  public @NotNull Map<Integer, LinkableData> getLeaderboard(
      @NotNull String stat, int page, int size, boolean inverted) {
    return null;
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
  public @NotNull Collection<Match> getMatches(
      int page, int size, @NotNull MatchStatus... statuses) {
    return new HashSet<>();
  }

  @Override
  public @NotNull BotLinkableData getDiscordUserData(long userId) {
    throw new UnsupportedOperationException("Linked data cannot be find using file loader");
  }

  @Override
  public @NotNull BotLinkableData getMemberData(long userId, long guildId) {
    throw new UnsupportedOperationException("Link data cannot be find using file loader");
  }

  @Override
  public @NotNull Collection<LinkableData> getLinks(@NotNull UserData user) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @NotNull Collection<LinkableData> getLinks(
      @NotNull UserData user, @NotNull LinkableDataType... types) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @NotNull
  @Override
  public Collection<AuthToken> getTokens(@NotNull UserData user) {
    throw new UnsupportedOperationException("File loader cannot get tokens");
  }

  @Override
  public @NotNull Collection<BotLinkableData> getDiscordData(long userId) {
    throw new UnsupportedOperationException("Links data cannot be find using file loader");
  }

  @Override
  public @Nullable AuthToken getAuthToken(@NotNull String token) {
    throw new UnsupportedOperationException("Auth tokens cannot be find using file loader");
  }
}
