package com.starfishst.bukkit.listeners.matches.creation;

import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.listeners.matches.MatchMakingListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import me.googas.api.client.data.TeamImpl;
import me.googas.api.client.data.TeamMemberImpl;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.TeamMember;
import me.googas.api.matches.TeamRole;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Creates teams by randomly selecting players TODO refactor */
public class RandomTeamCreation implements TeamCreation {

  @Override
  public void createTeams(@NotNull MatchMakingListener matchMaking, @NotNull Match match) {
    JsonClient connection = Guido.getClient().getConnection();
    Logger logger = Guido.getLogger();
    List<LinkedInfo> participantsCopy = new ArrayList<>(matchMaking.getParticipants());
    List<List<LinkedInfo>> teams = new ArrayList<>();
    List<Party> occupied = new ArrayList<>();
    Party observers = match.getDefaultParty();
    if (connection != null) {
      for (int i = 0; i < (matchMaking.getParticipants().size() / matchMaking.getPerTeam()); i++) {
        List<LinkedInfo> aTeam = RandomUtils.getRandom(participantsCopy, matchMaking.getPerTeam());
        teams.add(aTeam);
        participantsCopy.removeAll(aTeam);
      }
      logger.info("Teams have been randomly selected: " + teams);
      for (List<LinkedInfo> team : teams) {
        for (Party party : match.getParties()) {
          if (party != observers && !occupied.contains(party) && party instanceof Team) {
            occupied.add(party);
            Set<TeamMember> members = new HashSet<>();
            for (LinkedInfo info : team) {
              UUID uuid =
                  UUIDUtils.untrim(
                      info.getIdentification().getValidatedValue("uuid", String.class));
              MatchPlayer player = match.getPlayer(uuid);
              if (player != null) {
                match.setParty(player, party);
              }
              matchMaking.getToAdd().put(uuid, party);
              members.add(new TeamMemberImpl(info, TeamRole.NORMAL));
            }
            logger.info(party + " has been assigned to " + team);
            String name = "Team " + (teams.indexOf(team) + 1);
            ((Team) party).setName(name);
            connection.sendRequest(
                new Request<>(
                    Boolean.class,
                    "match-add-team",
                    Maps.objects("id", matchMaking.getMatchId())
                        .append("team", new TeamImpl(-3, name, members))
                        .build()),
                bol -> {
                  logger.info(
                      "Team " + name + " was added to " + matchMaking.getMatchId() + "? " + bol);
                });
            break;
          }
        }
      }
      match
          .needModule(StartMatchModule.class)
          .forceStartCountdown(Duration.ofSeconds(120), Duration.ZERO);
    }
  }

  @Override
  public void clear() {}
}
