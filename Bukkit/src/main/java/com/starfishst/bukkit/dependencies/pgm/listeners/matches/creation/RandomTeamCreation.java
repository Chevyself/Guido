package com.starfishst.bukkit.dependencies.pgm.listeners.matches.creation;

import com.starfishst.bukkit.client.requests.BukkitIntRequest;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.matches.HostedPlayer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.api.MessengerListenFailException;
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

  @NonNull
  private List<LinkableInfo> randomTeam(@NonNull List<HostedPlayer> random) {
    List<LinkableInfo> links = new ArrayList<>();
    for (HostedPlayer hostedPlayer : random) {
      links.add(hostedPlayer.toLink());
    }
    return links;
  }

  @Override
  public void createTeams(
      @NonNull PGMMatchMakingHandler listener,
      @NonNull PGMHostedMatch PGMHostedMatch,
      @NonNull Match match) {
    Set<HostedPlayer> participants = PGMHostedMatch.getParticipants();
    Map<Party, List<LinkableInfo>> teams = new HashMap<>();
    int perTeam = PGMHostedMatch.getDetails().getOr("players-per-team", Integer.class, 1);
    int index = 1;
    for (int i = 0; i < (PGMHostedMatch.getParticipants().size() / perTeam); i++) {
      Team party = this.getAvailableParty(teams, match);
      if (party == null) continue;
      List<LinkableInfo> aTeam = this.randomTeam(RandomUtils.getRandom(participants, perTeam));
      Set<TeamMember> members = new HashSet<>();
      teams.put(party, aTeam);
      for (LinkableInfo hosted : aTeam) {
        UUID uuid = UUIDUtils.untrim(hosted.getIdentification().validated("uuid", String.class));
        MatchPlayer player = match.getPlayer(uuid);
        if (player != null) {
          match.setParty(player, party);
        }
        listener.getToAdd().put(uuid, party);
        members.add(new SimpleTeamMember(hosted, TeamRole.NORMAL));
      }
      String name = "Team " + index;
      try {
        Integer id =
            new BukkitIntRequest(
                    "match-add-team",
                    Maps.objects("id", PGMHostedMatch.getId())
                        .append("team", new SimpleMatchTeam(-3, name, members)))
                .send();
        if (id != null) {
          PGMHostedMatch.getTeams().put(party.getId(), id);
        }
      } catch (MessengerListenFailException e) {
        e.printStackTrace();
      }
      index++;
      participants.removeAll(aTeam);
    }
    match
        .needModule(StartMatchModule.class)
        .forceStartCountdown(
            Duration.ofSeconds(PGMMatchMakingHandler.secondsToStart), Duration.ZERO);
  }

  @Override
  public void clear() {}
}
