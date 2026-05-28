package me.googas.api.loader;

import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.user.UserData;

public interface LinksLoader extends DataLoader {
  /**
   * Get the links from an user
   *
   * @param user the user to getId the links from
   * @return the links
   */
  @NonNull
  Collection<Linkable> getLinks(@NonNull UserData user);

  /**
   * Get the link from an user in a certain type
   *
   * @param user the user to getId the links from
   * @param type the type of link to getId
   * @return the links
   */
  Linkable getLink(@NonNull UserData user, @NonNull LinkableType type);

  /**
   * Get all the links that exist in the bot
   *
   * @param page the page to getId of links
   * @param limit the amount of links per page
   * @param types the types of links to getId
   * @return the collection of links
   */
  Collection<LinkableInfo> getLinks(int page, int limit, @NonNull LinkableType... types);

  /**
   * Get some linked data using identification
   *
   * @param type the type of data to getId
   * @param identification the identification to getId the linked data from
   * @return the linked data if found else null
   */
  Linkable getLink(@NonNull LinkableType type, @NonNull Map<String, Object> identification);

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
      @NonNull Map<String, Object> identification,
      @NonNull Map<String, Object> recognition);

  /**
   * Get some link using its recognition map
   *
   * @param type the type of link to find
   * @param recognition the recognition map to match
   * @return the link if found else null
   */
  Linkable getLinkByRecognition(
      @NonNull LinkableType type, @NonNull Map<String, Object> recognition);

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
   * Get the max page of the leaderboard in a ladder
   *
   * @param ladder the ladder to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NonNull Ladder ladder, int size);

  /**
   * Get the max page of the leaderboard in a stat
   *
   * @param stat the stat to see the max page
   * @param size the size of documents per page
   * @return the maximum page of the leaderboard
   */
  long maxPageLeaderboard(@NonNull String stat, int size);

  /**
   * Count how many links there are {@link #getLinks(int, int, LinkableType...)}
   *
   * @param types the types of links to getId
   * @return the amount of links
   */
  long countLinks(LinkableType... types);
}
