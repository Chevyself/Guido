package dev.xevy.guido.bukkit.pgm.listeners.matches.creation;

import dev.xevy.bukkit.GuidoBukkitRuntime;
import dev.xevy.bukkit.HostedPlayer;
import dev.xevy.guido.bukkit.pgm.PGMHostedMatch;
import dev.xevy.guido.bukkit.pgm.listeners.matches.PGMMatchMakingHandler;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.immutable.ImmutableMinecraftMatchTeam;
import me.googas.api.immutable.ImmutableMinecraftMatchTeamMember;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamRole;
import me.googas.api.utility.RandomUtils;
import me.googas.net.sockets.json.client.JsonClient;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

/** Creates teams by randomly selecting players */
public class RandomTeamCreation extends AbstractTeamCreation {

  @NonNull
  private static final Logger logger = Logger.getLogger(RandomTeamCreation.class.getName());

  public RandomTeamCreation(@NonNull GuidoBukkitRuntime runtime) {
    super(runtime);
  }

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
    int perTeam = hosted.getPlayersPerTeam();
    int index = 1;
    List<ImmutableMinecraftMatchTeam> requestTeams = new ArrayList<>();
    JsonClient connection = runtime.getConnection().orElse(null);
    if (connection == null) {
      logger.severe("Failed to create teams with random team selection");
      return;
    }
    for (int i = 0; i < (hosted.getParticipants().size() / perTeam); i++) {
      Team party = this.getAvailableParty(teams, match);
      if (party == null) continue;
      List<HostedPlayer> aTeam = RandomUtils.getRandom(left, perTeam);
      Set<ImmutableMinecraftMatchTeamMember> members = new HashSet<>();
      teams.put(party, aTeam);
      for (HostedPlayer hostedPlayer : aTeam) {
        members.add(new ImmutableMinecraftMatchTeamMember(hostedPlayer.getId(), TeamRole.MEMBER));
        this.setParty(hostedPlayer, party, match);
      }
      String name = "Team " + index;
      requestTeams.add(
          new ImmutableMinecraftMatchTeam(MatchTeam.NO_TEAM, members, name, party.getId()));
      party.setName(name);
      index++;
    }
    try {
      tryStartMatch(hosted, match, requestTeams, connection);
    } catch (ExecutionException | InterruptedException e) {
      logger.log(Level.SEVERE, "Failed to start match with random team creation", e);
    }
  }

  private static void tryStartMatch(
      @NonNull PGMHostedMatch hosted,
      @NonNull Match match,
      @NonNull List<ImmutableMinecraftMatchTeam> requestTeams,
      @NonNull JsonClient connection)
      throws ExecutionException, InterruptedException {
    List<ImmutableMinecraftMatchTeam> resultTeams =
        Requests.MinecraftMatches.setTeams(hosted.getId(), new Requests.SetTeamsData(requestTeams))
            .future(connection)
            .get()
            .getTeams();
    for (ImmutableMinecraftMatchTeam resultTeam : resultTeams) {
      resultTeam
          .getPgmPartyId()
          .ifPresent(
              pgmPartyId -> {
                hosted.getTeams().put(pgmPartyId, resultTeam);
              });
    }
    Requests.MinecraftMatches.updateStatus(hosted.getId(), MatchStatus.STARTING)
        .future(connection)
        .get();
    match
        .needModule(StartMatchModule.class)
        .forceStartCountdown(
            Duration.ofSeconds(PGMMatchMakingHandler.secondsToStart), Duration.ZERO);
  }

  @Override
  public void clear() {}
}
