package me.googas.api.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkedDataType;
import me.googas.api.links.LinkedInfo;
import me.googas.commons.cache.ICatchable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** This object represents a match which was played by one ore more teams */
public interface Match extends ICatchable {

  /**
   * Get whether the given information nis inside a team of this match
   *
   * @param type the type of link
   * @param identification the way to identify it
   * @return true if it matches a member from a team
   */
  default boolean isParticipating(@NotNull LinkedDataType type, @NotNull ValuesMap identification) {
    Collection<Team> teams = this.getTeams();
    for (Team team : teams) {
      for (TeamMember member : team.getMembers()) {
        LinkedInfo info = member.getLinkInfo();
        if (info.getType().equals(type)) {
          switch (type) {
            case NONE:
            case MINECRAFT:
            case DISCORD:
            default:
              if (info.getIdentification().matches(identification)) {
                return true;
              }
              break;
            case DISCORD_GUILD:
              if (info.getIdentification().equals(identification)) {
                return true;
              }
              break;
          }
        }
      }
    }
    return false;
  }

  /**
   * The unique id of the match
   *
   * @return the unique id of the match
   */
  @NotNull
  String getId();

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  long getGuildId();

  /**
   * Finishes the match
   *
   * @param winners the winners of the match
   */
  default void finish(@Nullable Team winners) {
    this.setWinners(winners);
    this.setStatus(MatchStatus.FINISHED);
  }

  /**
   * Get the teams that are participating in the match
   *
   * @return collection of teams
   */
  @NotNull
  Collection<Team> getTeams();

  /**
   * Get the team that won the match
   *
   * @return the team that won the match. This can return null in case that the match has not
   *     finished yet
   */
  @Nullable
  Team getWinners();

  /**
   * Get the details of the match
   *
   * @return the details of the match
   */
  @NotNull
  ValuesMap getDetails();

  /**
   * Set the winners of the match
   *
   * @param winners the winners of the match
   */
  void setWinners(@Nullable Team winners);

  /**
   * Set the status of the match
   *
   * @param status the new status of the match
   */
  void setStatus(@NotNull MatchStatus status);

  /**
   * Get the status of the match
   *
   * @return the status of the match
   */
  @NotNull
  MatchStatus getStatus();

  /**
   * Get the participants of a match. This collection must be immutable
   *
   * @return the collection of participants
   */
  @NotNull
  default Collection<LinkedInfo> getParticipants() {
    Set<LinkedInfo> participants = new HashSet<>();
    for (Team team : this.getTeams()) {
      for (TeamMember member : team.getMembers()) {
        participants.add(member.getLinkInfo());
      }
    }
    return Collections.unmodifiableSet(participants);
  }

  @Override
  @NotNull
  Match refresh();

  /**
   * Get a team that is playing this match by its name
   *
   * @param name the name of the team to get
   * @return the team if the name matches else null
   */
  @Nullable
  default Team getTeam(@NotNull String name) {
    for (Team team : this.getTeams()) {
      if (team.getName().equalsIgnoreCase(name)) {
        return team;
      }
    }
    return null;
  }
}
