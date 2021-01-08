package me.googas.api.server.receptors;

import java.util.Collection;
import java.util.HashSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.loader.MatchLoader;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class MatchReceptors {

  @NonNull private final MatchLoader loader;
  @NonNull @Getter @Setter private MatchSupplier matchSupplier;
  @NonNull @Getter @Setter private LadderSupplier ladderSupplier;

  public MatchReceptors(
      @NonNull MatchLoader loader,
      @NonNull MatchSupplier matchSupplier,
      @NonNull LadderSupplier ladderSupplier) {
    this.loader = loader;
    this.matchSupplier = matchSupplier;
    this.ladderSupplier = ladderSupplier;
  }

  @Receptor("match/create")
  public Match create(
      @ParamName("guild") long guild,
      @ParamName("teams") Collection<MatchTeam> teams,
      @ParamName("details") ValuesMap details) {
    return this.matchSupplier.create(guild, teams, details);
  }

  @Receptor("match/finish")
  public boolean finish(@ParamName("id") String id, @ParamName("team") int team) {
    Match match = this.getMatch(id);
    if (match == null) return false;
    match.finish(match.getTeam(team));
    return true;
  }

  @Receptor("match/add-team")
  public int addTeam(@ParamName("id") String id, @ParamName("team") MatchTeam matchTeam) {
    Match match = this.getMatch(id);
    if (matchTeam.getId() == -3) {
      SimpleMatchTeam guidoTeam =
          new SimpleMatchTeam(
              match.nextTeamId(), matchTeam.getName(), new HashSet<>(matchTeam.getMembers()));
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
    Match match = this.getMatch(id);
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

  @Receptor("match/ladder")
  public Ladder ladder(@ParamName("name") String name) {
    return this.ladderSupplier.getLadder(name);
  }

  public Match getMatch(@NonNull String id) {
    return this.loader.getMatch(id);
  }

  public interface MatchSupplier {
    @NonNull
    Match create(long guild, @NonNull Collection<MatchTeam> teams, @NonNull ValuesMap details);
  }

  public interface LadderSupplier {
    @Nullable
    Ladder getLadder(@NonNull String name);
  }
}
