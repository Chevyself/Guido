package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.matches.HostedPlayer;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.messaging.json.client.JsonClient;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Creates teams by randomly selecting players */
public class RandomTeamCreation implements TeamCreation {

  private Team getAvailableParty(Map<Party, List<HostedPlayer>> teams, Match match) {
    Party observers = match.getDefaultParty();
    for (Party party : match.getParties()) {
      if (party != observers && this.notOccupied(teams, party) && party instanceof Team) {
        return (Team) party;
      }
    }

    return null;
  }

  /**
   * Check if a party is not occupied
   *
   * @param teams the map of teams which contains all the teams and their parties
   * @param party the party to check if it is not occupied
   * @return true if the match is not occupied
   */
  private boolean notOccupied(@NonNull Map<Party, List<HostedPlayer>> teams, @NonNull Party party) {
    for (Party occupied : teams.keySet()) {
      if (occupied.equals(party)) return false;
    }
    return true;
  }

  @Override
  public void createTeams(
      @NonNull PGMMatchMakingHandler listener,
      @NonNull PGMHostedMatch hosted,
      @NonNull Match match) {
    Set<HostedPlayer> left = new HashSet<>(hosted.getParticipants());
    Map<Party, List<HostedPlayer>> teams = new HashMap<>();
    int perTeam = hosted.getInt(null, "players-per-team", 1);
    int index = 1;
    for (int i = 0; i < (hosted.getParticipants().size() / perTeam); i++) {
      Team party = this.getAvailableParty(teams, match);
      if (party == null) continue;
      List<HostedPlayer> aTeam = RandomUtils.getRandom(left, perTeam);
      Set<TeamMember> members = new HashSet<>();
      teams.put(party, aTeam);
      for (HostedPlayer hostedPlayer : aTeam) {
        members.add(new TeamMember(hostedPlayer.toLink(), TeamRole.NORMAL));
        this.setParty(hostedPlayer, party, match);
      }
      String name = "Team " + index;
      JsonClient connection = Guido.getClient().getConnection();
      Requests.Matches.addTeam(hosted.getId(), new MatchTeam(-3, members, name))
          .send(
              connection,
              optional ->
                  optional.ifPresent(
                      id -> {
                        if (id == -4) id = RandomUtils.getRandom().nextInt();
                        hosted.getTeams().put(party.getId(), new MatchTeam(id, members, name));
                      }));
      Requests.Matches.status(hosted.getId(), MatchStatus.STARTING).queue(connection);
      party.setName(name);
      index++;
    }
    match
        .needModule(StartMatchModule.class)
        .forceStartCountdown(
            Duration.ofSeconds(PGMMatchMakingHandler.secondsToStart), Duration.ZERO);
  }

  @Override
  public void clear() {}
}
