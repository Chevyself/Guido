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
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.messaging.json.client.JsonClient;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Creates teams by randomly selecting players TODO refactor */
public class RandomTeamCreation implements TeamCreation {

  private Team getAvailableParty(Map<Party, List<LinkableInfo>> teams, Match match) {
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
  private boolean notOccupied(@NonNull Map<Party, List<LinkableInfo>> teams, @NonNull Party party) {
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
    Set<LinkableInfo> left = HostedPlayer.toInfo(hosted.getParticipants());
    Map<Party, List<LinkableInfo>> teams = new HashMap<>();
    int perTeam = hosted.getDetails().getOr("players-per-team", Integer.class, 1);
    int index = 1;
    for (int i = 0; i < (hosted.getParticipants().size() / perTeam); i++) {
      Team party = this.getAvailableParty(teams, match);
      if (party == null) continue;
      List<LinkableInfo> aTeam = RandomUtils.getRandom(left, perTeam);
      Set<TeamMember> members = new HashSet<>();
      teams.put(party, aTeam);
      for (LinkableInfo link : aTeam) {
        UUID uuid = UUIDUtils.untrim(link.getIdentification().validated("uuid", String.class));
        MatchPlayer player = match.getPlayer(uuid);
        if (player != null) {
          match.setParty(player, party);
        }
        members.add(new SimpleTeamMember(link, TeamRole.NORMAL));
      }
      String name = "Team " + index;
      JsonClient connection = Guido.getClient().getConnection();
      Requests.Matches.addTeam(hosted.getId(), new SimpleMatchTeam(-3, name, members))
          .send(
              connection,
              optional -> {
                optional.ifPresent(
                    id ->
                        hosted
                            .getTeams()
                            .put(party.getId(), new SimpleMatchTeam(id, name, members)));
              });
      Requests.Matches.status(hosted.getId(), MatchStatus.STARTING).queue(connection);
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
