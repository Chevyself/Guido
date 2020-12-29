package me.googas.bot.core.server.receptors.matches;

import java.util.HashSet;
import lombok.NonNull;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.bot.core.matches.GuidoMatch;
import me.googas.bot.core.matches.GuidoMatchTeam;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class MatchReceptors {

  @Receptor("match/create")
  public Match create(
      @ParamName("guild") long guild,
      @ParamName("teams") HashSet<MatchTeam> teams,
      @ParamName("details") GuidoLinkedValuesMap details) {
    return new GuidoMatch(guild, teams, details).cache();
  }

  @Receptor("match/finish")
  public boolean finish(@ParamName("id") String id, @ParamName("team") int team) {
    Match match = Guido.getDataLoader().getMatch(id);
    if (match == null) return false;
    match.finish(match.getTeam(team));
    return true;
  }

  @Receptor("match/add-team")
  public int addTeam(@ParamName("id") String id, @ParamName("team") MatchTeam matchTeam) {
    Match match = this.getMatch(id);
    if (matchTeam.getId() == -3) {
      GuidoMatchTeam guidoTeam =
          new GuidoMatchTeam(match.nextTeamId(), matchTeam.getMembers(), matchTeam.getName());
      if (match.addTeam(guidoTeam)) {
        return guidoTeam.getId();
      }
    } else {
      if (match.addTeam(matchTeam)) {
        return matchTeam.getId();
      }
    }
    return -4;
  }

  @Receptor("match/remove-team")
  public boolean removeTeam(@ParamName("id") String id, @ParamName("team") int teamId) {
    Match match = this.getMatch(id);
    if (match != null) {
      MatchTeam matchTeam = match.getTeam(teamId);
      if (matchTeam != null) {
        return match.removeTeam(matchTeam);
      }
    }
    return false;
  }

  @Receptor("match/status")
  public boolean status(@ParamName("id") String id, @ParamName("status") MatchStatus status) {
    Match match = Guido.getDataLoader().getMatch(id);
    if (match != null) {
      match.setStatus(status);
      return true;
    }
    return false;
  }

  @Receptor("match/detail")
  public boolean detail(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Match match = this.getMatch(id);
    if (match == null) return false;
    match.getDetails().put(key, value);
    return true;
  }

  @Receptor("match/remove-detail")
  public boolean detail(@ParamName("id") String id, @ParamName("key") String key) {
    Match match = this.getMatch(id);
    if (match == null) return false;
    match.getDetails().remove(key);
    return true;
  }

  public Match getMatch(@NonNull String id) {
    return Guido.getDataLoader().getMatch(id);
  }
}
