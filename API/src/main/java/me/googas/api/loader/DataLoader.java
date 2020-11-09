package me.googas.api.loader;

import java.util.Collection;
import java.util.List;
import me.googas.api.discord.GuildData;
import me.googas.api.discord.RoleData;
import me.googas.api.links.LinkableData;
import me.googas.api.links.LinkableDataType;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.permissions.Group;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.RandomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Loads the data. */
public interface DataLoader {

  /**
   * Load the data of a guild. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @NotNull
  GuildData getGuildDataOrCreate(long id);

  /**
   * Load the data of a guild
   *
   * @param id the id of the guild
   * @return the data of the guild or null if not found
   */
  @Nullable
  GuildData getGuildData(long id);

  /**
   * Load the data of a role. If the data cannot be loaded create a fallback but don't return null
   *
   * @param id the id of the role
   * @param guildId the guild id from which the data of the role must be gotten
   * @return the data of the role or null if not found
   */
  @NotNull
  RoleData getRoleData(long id, long guildId);

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  @Nullable
  UserData getUserData(@Nullable String id);

  /**
   * Get an auth token using its unique string
   *
   * @param token the unique string of the token
   * @return the token if found else null
   */
  @Nullable
  AuthToken getAuthToken(@NotNull String token);

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
  @NotNull
  Collection<LinkableData> getLinks(@NotNull UserData user);

  /**
   * Get the links from an user in a certain type
   *
   * @param user the user to get the links from
   * @param types the types to get the team from
   * @return the links
   */
  @NotNull
  Collection<LinkableData> getLinks(@NotNull UserData user, @NotNull LinkableDataType... types);
  /**
   * Get the tokens from an user
   *
   * @param user the user to get the tokens from
   * @return the tokens gene rated by an user
   */
  @NotNull
  Collection<AuthToken> getTokens(@NotNull UserData user);

  /**
   * Get some linked data using identification
   *
   * @param type the type of data to get
   * @param identification the identification to get the linked data from
   * @param equal whether to equal or match the identification
   * @return the linked data if found else null
   */
  @Nullable
  LinkableData getLinkedData(
      @NotNull LinkableDataType type, @NotNull ValuesMap identification, boolean equal);

  /**
   * Get a match using its id
   *
   * @param id the id of the match
   * @return the id of the match
   */
  @Nullable
  Match getMatch(@NotNull String id);

  /**
   * Get all the matches in which a link is participating
   *
   * @param type the type of link
   * @param identification the way to identify the link
   * @param status the statutes that must match
   * @return the matches in which the link is participating and have the given status
   */
  @NotNull
  Collection<Match> getParticipating(
      @NotNull LinkableDataType type,
      @NotNull ValuesMap identification,
      @NotNull MatchStatus... status);

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NotNull
  default String nextUserId() {
    String id = RandomUtils.nextString(6);
    UserData user = this.getUserData(id);
    while (user != null) {
      id = RandomUtils.nextString(6);
      user = this.getUserData(id);
    }
    return id;
  }

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NotNull
  default String nextMatchId() {
    String id = RandomUtils.nextString(16);
    Match user = this.getMatch(id);
    while (user != null) {
      id = RandomUtils.nextString(16);
      user = this.getMatch(id);
    }
    return id;
  }

  /**
   * Get a new id for a group
   *
   * @return the id of the new group
   */
  @NotNull
  default String nextGroupId() {
    String id = RandomUtils.nextString(6);
    Group group = this.getGroup(id);
    while (group != null) {
      id = RandomUtils.nextString(6);
      group = this.getGroup(id);
    }
    return id;
  }

  /**
   * Get a group matching the given id
   *
   * @param id the id of the group
   * @return the group if found else null
   */
  @Nullable
  Group getGroup(@NotNull String id);

  /**
   * Delete the group with the given id
   *
   * @param id the id of the group to delete
   * @return true if the group was deleted
   */
  boolean deleteGroup(String id);

  /**
   * Get all the created groups
   *
   * @return the created groups
   */
  @NotNull
  Collection<Group> getGroups();

  /**
   * Get the leaderboard in certain ladder
   *
   * @param ladder the ladder to look the leaderboard from
   * @param page the page to see of the leaderboard
   * @param size the size to show of the leaderboard
   * @return the leaderboard
   */
  @NotNull
  List<LinkableData> getLeaderboard(@NotNull Ladder ladder, int page, int size);

  /**
   * Get the leaderboard for certain stat
   *
   * @param stat the stat to look the leaderboard from
   * @param page the page to see of the leaderboard
   * @param size the size to show of the leaderboard
   * @param inverted whether the leaderboard should be inverted this means low to high
   * @return the leaderboard
   */
  @NotNull
  List<LinkableData> getLeaderboard(@NotNull String stat, int page, int size, boolean inverted);

  /**
   * Get all the matches
   *
   * @param page the page of matches to see
   * @param size the size of the page
   * @param statuses the status of the matches to get
   * @return the collection of matches
   */
  @NotNull
  Collection<Match> getMatches(int page, int size, @NotNull MatchStatus... statuses);
}
