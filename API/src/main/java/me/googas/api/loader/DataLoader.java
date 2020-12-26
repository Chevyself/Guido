package me.googas.api.loader;

import java.util.Collection;
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
import me.googas.commons.RandomUtils;

/** Loads the data. */
public interface DataLoader {

  /**
   * Load the data of an user
   *
   * @param id the id of the user
   * @return the data of the user or null if not found
   */
  UserData getUserData(String id);

  /**
   * Get an auth token using its unique string
   *
   * @param token the unique string of the token
   * @return the token if found else null
   */
  AuthToken getAuthToken(@NonNull String token);

  /**
   * Get the links from an user
   *
   * @param user the user to get the links from
   * @return the links
   */
  @NonNull
  Collection<Linkable> getLinks(@NonNull UserData user);

  /**
   * Get the links from an user in a certain type
   *
   * @param user the user to get the links from
   * @param types the types to get the team from
   * @return the links
   */
  @NonNull
  Collection<Linkable> getLinks(@NonNull UserData user, @NonNull LinkableType... types);

  /**
   * Get all the links that exist in the bot
   *
   * @param page the page to get of links
   * @param limit the amount of links per page
   * @param types the types of links to get
   * @return the collection of links
   */
  Collection<LinkableInfo> getLinks(int page, int limit, @NonNull LinkableType... types);

  /**
   * Get the tokens from an user
   *
   * @param user the user to get the tokens from
   * @return the tokens gene rated by an user
   */
  @NonNull
  Collection<AuthToken> getTokens(@NonNull UserData user);

  /**
   * Get some linked data using identification
   *
   * @param type the type of data to get
   * @param identification the identification to get the linked data from
   * @return the linked data if found else null
   */
  Linkable getLink(@NonNull LinkableType type, @NonNull ValuesMap identification);

  /**
   * Get some link using an identification map and a recognition map
   *
   * @param type the type of the link to find
   * @param identification the identification map
   * @param recognition the recognition map
   * @return the link if found else null
   */
  Linkable getLink(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull ValuesMap recognition);

  /**
   * Get some link using its recognition map
   *
   * @param type the type of link to find
   * @param recognition the recognition map to match
   * @return the link if found else null
   */
  Linkable getLinkByRecognition(@NonNull LinkableType type, @NonNull ValuesMap recognition);

  /**
   * Get a match using its id
   *
   * @param id the id of the match
   * @return the id of the match
   */
  Match getMatch(@NonNull String id);

  /**
   * Get all the matches in which a link is participating
   *
   * @param type the type of link
   * @param identification the way to identify the link
   * @param status the statutes that must match
   * @return the matches in which the link is participating and have the given status
   */
  @NonNull
  Collection<Match> getParticipating(
      @NonNull LinkableType type,
      @NonNull ValuesMap identification,
      @NonNull MatchStatus... status);

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NonNull
  default String nextUserId() {
    String id = RandomUtils.nextString(6);
    if (this.getUserData(id) != null) return this.nextUserId();
    return id;
  }

  /**
   * Get a new id for an user
   *
   * @return the new id for an user
   */
  @NonNull
  default String nextMatchId() {
    String id = RandomUtils.nextString(16);
    if (this.getMatch(id) != null) return this.nextMatchId();
    return id;
  }

  /**
   * Get a new id for a group
   *
   * @return the id of the new group
   */
  @NonNull
  default String nextGroupId() {
    String id = RandomUtils.nextString(6);
    if (this.getGroup(id) != null) return this.nextGroupId();
    return id;
  }

  /**
   * Get a new id for a punishment
   *
   * @return the id of the new punishment
   */
  @NonNull
  default String nextPunishmentId() {
    String id = RandomUtils.nextString(6);
    if (this.getPunishment(id) != null) return this.nextPunishmentId();
    return id;
  }

  /**
   * Get a punishment by its id
   *
   * @param id the id of the punishment to match
   * @return the punishment
   */
  Punishment getPunishment(@NonNull String id);

  /**
   * Get a new id fr a team
   *
   * @return the new id fr the team
   */
  @NonNull
  default String nextTeamId() {
    String id = RandomUtils.nextString(6);
    if (this.getTeam(id) != null) return this.nextTeamId();
    return id;
  }

  /**
   * Get a group matching the given id
   *
   * @param id the id of the group
   * @return the group if found else null
   */
  Group getGroup(@NonNull String id);

  /**
   * Delete the group with the given id
   *
   * @param id the id of the group to delete
   * @return true if the group was deleted
   */
  boolean deleteGroup(String id);

  /**
   * Count how many links there are {@link #getLinks(int, int, LinkableType...)}
   *
   * @param types the types of links to get
   * @return the amount of links
   */
  long countLinks(LinkableType... types);

  /**
   * Get all the created groups but only the information of them
   *
   * @param page the page of groups to see
   * @param size the size of the groups per page
   * @return the created groups
   */
  @NonNull
  Collection<GroupInfo> getGroups(int page, int size);

  /**
   * Get the leaderboard in certain ladder
   *
   * @param ladder the ladder to look the leaderboard from
   * @param page the page to see of the leaderboard
   * @param size the size to show of the leaderboard
   * @return the leaderboard
   */
  @NonNull
  Map<Integer, LinkableInfo> getLeaderboard(@NonNull Ladder ladder, int page, int size);

  /**
   * Get how many groups there are
   *
   * @return the amount of groups that there is
   */
  long countGroups();

  /**
   * Get the leaderboard for certain stat
   *
   * @param stat the stat to look the leaderboard from
   * @param page the page to see of the leaderboard
   * @param size the size to show of the leaderboard
   * @param inverted whether the leaderboard should be inverted this means low to high
   * @return the leaderboard
   */
  @NonNull
  Map<Integer, LinkableInfo> getLeaderboard(
      @NonNull String stat, int page, int size, boolean inverted);

  /**
   * Get all the matches
   *
   * @param page the page of matches to see
   * @param size the size of the page
   * @param statuses the status of the matches to get
   * @return the collection of matches
   */
  @NonNull
  Collection<MatchInfo> getMatches(int page, int size, @NonNull MatchStatus... statuses);

  /**
   * Delete a team by using its id
   *
   * @param id the id of the team to delete
   * @return whether the team was deleted
   */
  boolean deleteTeam(@NonNull String id);

  /**
   * Get a team by its id
   *
   * @param id the id of the team
   * @return the team if found else null
   */
  Team getTeam(@NonNull String id);

  /**
   * Get a team by its name
   *
   * @param name the name of the team
   * @return the team if found else null
   */
  Team getTeamByName(@NonNull String name);

  /**
   * Get the team in which a linkable is on
   *
   * @param linkable the linkable to get the team
   * @return the team if found else null
   */
  Team getTeam(@NonNull Linkable linkable);

  /**
   * Get all the created groups
   *
   * @return the created groups
   */
  @NonNull
  Collection<GroupInfo> getGroups();
}
