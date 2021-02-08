package me.googas.api.matches;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.API;
import me.googas.api.GuidoCatchable;
import me.googas.api.Informative;
import me.googas.api.events.match.MatchAddTeamEvent;
import me.googas.api.events.match.MatchPreAddTeamEvent;
import me.googas.api.events.match.MatchPreRemoveTeamEvent;
import me.googas.api.events.match.MatchRemoveTeamEvent;
import me.googas.api.events.match.MatchStatusUpdatedEvent;
import me.googas.api.events.match.MatchUnloadedEvent;
import me.googas.api.events.match.MatchWinnersSetEvent;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.team.TeamMember;
import me.googas.commons.RandomUtils;

/** This object represents a match which was played by one ore more teams */
public class AbstractMatch implements GuidoCatchable, Informative {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final Map<String, Map<String, Object>> information;
  @NonNull @Getter private final Set<MatchTeam> teams;
  @NonNull @Getter private MatchStatus status;
  @Nullable @Getter private MatchTeam winners;

  /**
   * Create the match
   *
   * @param id the id of the match
   * @param information the information about the match
   * @param teams the teams inside the match
   * @param status the status of the match
   * @param winners the winners of the match
   */
  public AbstractMatch(
      @NonNull String id,
      @NonNull Map<String, Map<String, Object>> information,
      @NonNull Set<MatchTeam> teams,
      @NonNull MatchStatus status,
      @Nullable MatchTeam winners) {
    this.id = id;
    this.information = information;
    this.teams = teams;
    this.status = status;
    this.winners = winners;
  }

  /** @deprecated this constructor may only be used by gson */
  public AbstractMatch() {
    this("", new HashMap<>(), new HashSet<>(), MatchStatus.VOIDED, null);
  }

  public AbstractMatch(
      @NonNull Map<String, Map<String, Object>> information,
      @NonNull Set<MatchTeam> teams,
      @NonNull MatchStatus status,
      @Nullable MatchTeam winners) {
    this(API.getLoader().getMatches().nextMatchId(), information, teams, status, winners);
  }

  /**
   * Get whether the given information is inside a team of this match
   *
   * @param type the type of link
   * @param identification the way to identify it
   * @return true if it matches a member from a team
   */
  public boolean isParticipating(
      @NonNull LinkableType type, @NonNull Map<String, Object> identification) {
    Collection<MatchTeam> matchTeams = this.getTeams();
    for (MatchTeam matchTeam : matchTeams) {
      for (TeamMember member : matchTeam.getMembers()) {
        if (member.getLink().compare(type, identification)) return true;
      }
    }
    return false;
  }

  /**
   * Finishes the match
   *
   * @param winners the winners of the match
   */
  public void finish(MatchTeam winners) {
    this.setWinners(winners);
    this.setStatus(MatchStatus.FINISHED);
  }

  /**
   * Get a team that is playing this match by its name
   *
   * @param name the name of the team to getId
   * @return the team if the name matches else null
   */
  public MatchTeam getTeam(@NonNull String name) {
    for (MatchTeam matchTeam : this.getTeams()) {
      if (matchTeam.getName().equalsIgnoreCase(name)) {
        return matchTeam;
      }
    }
    return null;
  }

  /**
   * Add a matchTeam to this match
   *
   * @param matchTeam the matchTeam to add
   * @return whether the matchTeam was added
   */
  public boolean addTeam(@NonNull MatchTeam matchTeam) {
    if (new MatchPreAddTeamEvent(this, matchTeam).not() && this.teams.add(matchTeam)) {
      new MatchAddTeamEvent(this, matchTeam).call();
      return true;
    }
    return false;
  }

  /**
   * Removes a matchTeam from this match
   *
   * @param matchTeam the matchTeam to remove
   * @return whether the matchTeam was removed
   */
  public boolean removeTeam(@NonNull MatchTeam matchTeam) {
    if (new MatchPreRemoveTeamEvent(this, matchTeam).not()
        && this.teams.removeIf(team -> team.getId() == matchTeam.getId())) {
      new MatchRemoveTeamEvent(this, matchTeam);
      return true;
    }
    return false;
  }

  /**
   * Get the next unused id for a team
   *
   * @return the unused team id
   */
  public int nextTeamId() {
    int i = RandomUtils.getRandom().nextInt();
    MatchTeam given = this.getTeam(i);
    if (given == null) {
      return i;
    } else {
      return this.nextTeamId();
    }
  }

  /**
   * Get a team that is playing this match by its id
   *
   * @param id the id of the team to getId
   * @return the team if the id matches else null
   */
  public MatchTeam getTeam(int id) {
    for (MatchTeam matchTeam : this.getTeams()) {
      if (matchTeam.getId() == id) {
        return matchTeam;
      }
    }
    return null;
  }

  @NonNull
  public AbstractMatch setWinners(@Nullable MatchTeam winners) {
    this.winners = winners;
    new MatchWinnersSetEvent(this, winners).call();
    return this;
  }

  @NonNull
  public AbstractMatch setStatus(MatchStatus status) {
    if (new MatchStatusUpdatedEvent(this, status).not()) {
      this.status = status;
    }
    return this;
  }

  /**
   * Get the id of the guild where this match occurred
   *
   * @return the id of the guild
   */
  @Deprecated
  public long getGuildId() {
    return -1;
  }

  /**
   * Get the ladder in which the match was played
   *
   * @return the ladder where the match was played
   */
  @Nullable
  public Ladder getLadder() {
    return API.getLoader().getLadders().getLadder(this.getString(null, "ladder", ""));
  }

  /**
   * Get the participants of a match. This collection must be immutable
   *
   * @return the collection of participants
   */
  @NonNull
  public Collection<LinkableInfo> getParticipants() {
    Set<LinkableInfo> participants = new HashSet<>();
    for (MatchTeam matchTeam : this.getTeams()) {
      for (TeamMember member : matchTeam.getMembers()) {
        participants.add(member.getLink());
      }
    }
    return Collections.unmodifiableSet(participants);
  }

  @Override
  public void onRemove() {
    new MatchUnloadedEvent(this).call();
  }

  @Override
  public @NonNull AbstractMatch cache() {
    return (AbstractMatch) GuidoCatchable.super.cache();
  }
}
