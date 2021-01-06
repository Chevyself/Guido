package me.googas.api.loader;

import java.util.Collection;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.commons.RandomUtils;

public interface MatchLoader extends DataLoader {

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
  default String nextMatchId() {
    String id = RandomUtils.nextString(16);
    if (this.getMatch(id) != null) return this.nextMatchId();
    return id;
  }

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
}
