package com.starfishst.bukkit.listeners.pgm.matches.creation;

import com.starfishst.bukkit.client.BukkitIntRequest;
import com.starfishst.bukkit.listeners.pgm.matches.HostedMatch;
import com.starfishst.bukkit.listeners.pgm.matches.PGMMatchMakingListener;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.googas.api.client.data.SimpleTeam;
import me.googas.api.client.data.SimpleTeamMember;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Creates teams by randomly selecting players TODO refactor */
public class RandomTeamCreation implements TeamCreation {

  @Nullable
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
  private boolean notOccupied(@NotNull Map<Party, List<LinkableInfo>> teams, @NotNull Party party) {
    for (Party occupied : teams.keySet()) {
      if (occupied.equals(party)) return false;
    }
    return true;
  }

  @Override
  public void createTeams(
      @NotNull PGMMatchMakingListener listener,
      @NotNull HostedMatch hostedMatch,
      @NotNull Match match) {
    List<LinkableInfo> participants = hostedMatch.getParticipants();
    Map<Party, List<LinkableInfo>> teams = new HashMap<>();
    int perTeam = hostedMatch.getDetails().getOr("players-per-team", Integer.class, 1);
    int index = 1;
    for (int i = 0; i < (hostedMatch.getParticipants().size() / perTeam); i++) {
      Team party = this.getAvailableParty(teams, match);
      List<LinkableInfo> aTeam = RandomUtils.getRandom(participants, perTeam);
      Set<TeamMember> members = new HashSet<>();
      teams.put(party, aTeam);
      for (LinkableInfo info : aTeam) {
        UUID uuid = UUIDUtils.untrim(info.getIdentification().validated("uuid", String.class));
        MatchPlayer player = match.getPlayer(uuid);
        if (player != null) {
          match.setParty(player, party);
        }
        listener.getToAdd().put(uuid, party);
        members.add(new SimpleTeamMember(info, TeamRole.NORMAL));
      }
      String name = "Team " + index;
      new BukkitIntRequest(
              "match-add-team",
              Maps.objects("id", hostedMatch.getId())
                  .append("team", new SimpleTeam(-3, name, members)))
          .send(id -> hostedMatch.getTeams().put(party.getId(), id));
      index++;
      participants.removeAll(aTeam);
    }
    match
        .needModule(StartMatchModule.class)
        .forceStartCountdown(
            Duration.ofSeconds(PGMMatchMakingListener.secondsToStart), Duration.ZERO);
  }

  @Override
  public void clear() {}
}
