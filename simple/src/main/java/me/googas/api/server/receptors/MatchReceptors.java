package me.googas.api.server.receptors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.Requests;
import me.googas.api.loader.MatchLoader;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;

public class MatchReceptors {

  @NonNull private final MatchLoader loader;

  public MatchReceptors(@NonNull MatchLoader loader) {
    this.loader = loader;
  }

  @Receptor(Requests.Matches.MATCH)
  public AbstractMatch getMatch(@NonNull String id) {
    return this.loader.getMatch(id);
  }

  @Receptor(Requests.Matches.CREATE)
  public AbstractMatch create(
      @ParamName("information") Map<String, Map<String, Object>> information,
      @ParamName("teams") Set<MatchTeam> teams) {
    return new AbstractMatch(information, teams, MatchStatus.WAITING, null).cache();
  }

  @Receptor(Requests.Matches.FINISH)
  public boolean finish(@ParamName("id") String id, @ParamName("team") int team) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch == null) return false;
    abstractMatch.finish(abstractMatch.getTeam(team));
    return true;
  }

  @Receptor(Requests.Matches.ADD_TEAM)
  public int addTeam(@ParamName("id") String id, @ParamName("team") MatchTeam matchTeam) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch == null) return -4;
    if (matchTeam.getId() == -3) {
      MatchTeam guidoTeam =
          new MatchTeam(
              abstractMatch.nextTeamId(),
              new HashSet<>(matchTeam.getMembers()),
              matchTeam.getName());
      if (abstractMatch.addTeam(guidoTeam)) {
        return guidoTeam.getId();
      }
    } else {
      if (abstractMatch.addTeam(matchTeam)) {
        return matchTeam.getId();
      }
    }
    return -4;
  }

  @Receptor(Requests.Matches.REMOVE_TEAM)
  public boolean removeTeam(@ParamName("id") String id, @ParamName("team") int teamId) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch != null) {
      MatchTeam matchTeam = abstractMatch.getTeam(teamId);
      if (matchTeam != null) {
        return abstractMatch.removeTeam(matchTeam);
      }
    }
    return false;
  }

  @Receptor(Requests.Matches.STATUS)
  public boolean status(@ParamName("id") String id, @ParamName("status") MatchStatus status) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch != null) {
      abstractMatch.setStatus(status);
      return true;
    }
    return false;
  }

  @Receptor(Requests.Matches.DETAIL)
  public boolean detail(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch == null) return false;
    abstractMatch.set(null, key, value);
    return true;
  }

  @Receptor(Requests.Matches.REMOVE_DETAIL)
  public boolean detail(@ParamName("id") String id, @ParamName("key") String key) {
    AbstractMatch abstractMatch = this.getMatch(id);
    if (abstractMatch == null) return false;
    abstractMatch.set(null, key, null);
    return true;
  }

  @Receptor(Requests.Matches.LADDER)
  public Ladder ladder(@ParamName("name") String name) {
    return API.getLoader().getLadders().getLadder(name);
  }
}
